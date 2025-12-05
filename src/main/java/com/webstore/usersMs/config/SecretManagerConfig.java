package com.webstore.usersMs.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración para cargar secretos desde Google Cloud Secret Manager
 * Lee dos secretos:
 * 1. 'postgres_auth': Configuración completa de la base de datos (URL, username, host, etc.)
 * 2. 'passwirk_sec': Contraseña de la base de datos (tiene prioridad sobre password en postgres_auth)
 * 
 * Se ejecuta ANTES de que Spring Boot cargue la configuración del datasource
 */
@Log4j2
public class SecretManagerConfig implements EnvironmentPostProcessor {

    private static final String PROJECT_ID = "649345514504";
    private static final String SECRET_NAME = "postgres_auth";
    private static final String PASSWORD_SECRET_NAME = "passwirk_sec";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Solo cargar secretos si el perfil es 'prod'
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProd = activeProfiles.length > 0 && 
                        java.util.Arrays.asList(activeProfiles).contains("prod");
        
        if (!isProd) {
            log.debug("Perfil no es 'prod', omitiendo carga de secretos desde GCP");
            return;
        }

        // Verificar si está habilitado
        String enabled = environment.getProperty("gcp.secret-manager.enabled", "true");
        if (!Boolean.parseBoolean(enabled)) {
            log.info("Secret Manager está deshabilitado. Usando variables de entorno estándar.");
            return;
        }

        Map<String, Object> allProperties = new HashMap<>();
        
        try {
            // Cargar secreto principal postgres_auth
            log.info("Intentando cargar secreto '{}' del proyecto '{}'", SECRET_NAME, PROJECT_ID);
            
            String secretValue = getSecretFromGCP(PROJECT_ID, SECRET_NAME);
            
            if (secretValue != null && !secretValue.isEmpty()) {
                Map<String, Object> properties = parseSecret(secretValue);
                allProperties.putAll(properties);
                log.info("Secreto '{}' cargado exitosamente", SECRET_NAME);
            } else {
                log.warn("El secreto '{}' está vacío o no se pudo cargar", SECRET_NAME);
            }
            
        } catch (Exception e) {
            log.warn("Error al cargar secreto '{}': {}. Continuando...", SECRET_NAME, e.getMessage());
        }
        
        try {
            // Cargar secreto de contraseña passwirk_sec (tiene prioridad sobre password en postgres_auth)
            log.info("Intentando cargar secreto de contraseña '{}' del proyecto '{}'", PASSWORD_SECRET_NAME, PROJECT_ID);
            
            String passwordSecret = getSecretFromGCP(PROJECT_ID, PASSWORD_SECRET_NAME);
            
            if (passwordSecret != null && !passwordSecret.isEmpty()) {
                // El secreto puede ser solo la contraseña o un JSON
                String password = extractPasswordFromSecret(passwordSecret);
                if (password != null && !password.isEmpty()) {
                    allProperties.put("spring.datasource.password", password);
                    log.info("Contraseña cargada desde secreto '{}'", PASSWORD_SECRET_NAME);
                } else {
                    log.warn("No se pudo extraer contraseña del secreto '{}'", PASSWORD_SECRET_NAME);
                }
            } else {
                log.warn("El secreto de contraseña '{}' está vacío o no se pudo cargar", PASSWORD_SECRET_NAME);
            }
            
        } catch (Exception e) {
            log.warn("Error al cargar secreto de contraseña '{}': {}. Usando contraseña de postgres_auth si está disponible.", 
                    PASSWORD_SECRET_NAME, e.getMessage());
        }
        
        // Aplicar todas las propiedades configuradas
        if (!allProperties.isEmpty()) {
            MapPropertySource propertySource = new MapPropertySource("gcp-secrets", allProperties);
            environment.getPropertySources().addFirst(propertySource);
            log.info("Propiedades de Secret Manager configuradas exitosamente");
        } else {
            log.warn("No se pudieron cargar propiedades desde Secret Manager");
        }
    }

    private String getSecretFromGCP(String projectId, String secretName) throws IOException {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            // Construir el nombre de la versión del secreto
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretName, "latest");
            
            // Crear la solicitud
            AccessSecretVersionRequest request = AccessSecretVersionRequest.newBuilder()
                    .setName(secretVersionName.toString())
                    .build();
            
            // Acceder al secreto
            AccessSecretVersionResponse response = client.accessSecretVersion(request);
            return response.getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            log.error("Error al acceder al secreto: {}", e.getMessage());
            throw e;
        }
    }

    private Map<String, Object> parseSecret(String secretValue) {
        Map<String, Object> properties = new HashMap<>();
        
        try {
            // Intentar parsear como JSON
            JsonNode jsonNode = objectMapper.readTree(secretValue);
            
            // Extraer valores del JSON
            String username = getJsonValue(jsonNode, "username", "user", "db_user");
            String password = getJsonValue(jsonNode, "password", "pass", "db_password");
            String url = getJsonValue(jsonNode, "url", "connection_url", "jdbc_url", "database_url");
            String database = getJsonValue(jsonNode, "database", "db", "db_name", "database_name");
            String host = getJsonValue(jsonNode, "host", "hostname");
            String port = getJsonValue(jsonNode, "port");
            String cloudSqlInstance = getJsonValue(jsonNode, "cloud_sql_instance", "cloudSqlInstance", "instance");
            
            // Agregar propiedades al mapa
            if (username != null) {
                properties.put("spring.datasource.username", username);
                log.debug("spring.datasource.username configurado desde secreto");
            }
            
            if (password != null) {
                properties.put("spring.datasource.password", password);
                log.debug("spring.datasource.password configurado desde secreto");
            }
            
            // Construir URL si no está completa
            if (url == null) {
                // Verificar variables de entorno para Cloud SQL Unix Socket
                // Cloud Run monta automáticamente el socket en /cloudsql/PROJECT-ID:REGION:INSTANCE-ID
                String dbSocket = System.getenv("DB_SOCKET");
                String cloudSqlInstanceFromEnv = System.getenv("CLOUD_SQL_INSTANCE");
                
                // Prioridad: cloudSqlInstance del secreto > CLOUD_SQL_INSTANCE env > DB_SOCKET env
                String instanceToUse = cloudSqlInstance != null ? cloudSqlInstance : cloudSqlInstanceFromEnv;
                
                // Si DB_SOCKET está disponible pero no tenemos instancia, extraerla del path
                if (instanceToUse == null && dbSocket != null && dbSocket.startsWith("/cloudsql/")) {
                    instanceToUse = dbSocket.replace("/cloudsql/", "");
                    log.debug("Instancia extraída de DB_SOCKET: {}", instanceToUse);
                }
                
                if (instanceToUse != null && database != null) {
                    // Formato para Cloud SQL con socket factory usando Unix Socket
                    // Cloud Run monta el socket en /cloudsql/PROJECT-ID:REGION:INSTANCE-ID
                    // El socket factory maneja automáticamente el socket Unix
                    url = String.format("jdbc:postgresql:///%s?cloudSqlInstance=%s&socketFactory=com.google.cloud.sql.postgres.SocketFactory",
                            database, instanceToUse);
                    log.debug("Usando Cloud SQL con socket factory e instancia: {}", instanceToUse);
                } else if (host != null) {
                    // Formato estándar (puede ser IP privada de VPC)
                    String dbName = database != null ? database : "postgres";
                    String dbPort = port != null ? port : "5432";
                    url = String.format("jdbc:postgresql://%s:%s/%s", host, dbPort, dbName);
                }
            }
            
            // Si se especifica una IP privada directamente, usarla
            String privateIp = getJsonValue(jsonNode, "private_ip", "privateIp", "vpc_ip");
            if (privateIp != null && url == null && database != null) {
                String dbPort = port != null ? port : "5432";
                url = String.format("jdbc:postgresql://%s:%s/%s", privateIp, dbPort, database);
                log.debug("Usando IP privada de VPC: {}", privateIp);
            }
            
            if (url != null) {
                properties.put("spring.datasource.url", url);
                log.debug("spring.datasource.url configurado desde secreto: {}", url.replaceAll("password=[^&]*", "password=***"));
            }
            
        } catch (Exception e) {
            // Si no es JSON, tratar como string simple (formato: username:password:url)
            log.warn("No se pudo parsear como JSON, intentando formato simple: {}", e.getMessage());
            parseSimpleFormat(secretValue, properties);
        }
        
        return properties;
    }

    private String getJsonValue(JsonNode node, String... keys) {
        for (String key : keys) {
            JsonNode value = node.get(key);
            if (value != null && !value.isNull()) {
                return value.asText();
            }
        }
        return null;
    }

    private void parseSimpleFormat(String secretValue, Map<String, Object> properties) {
        // Formato esperado: username:password:url o username:password
        String[] parts = secretValue.split(":");
        if (parts.length >= 2) {
            properties.put("spring.datasource.username", parts[0]);
            properties.put("spring.datasource.password", parts[1]);
            if (parts.length >= 3) {
                properties.put("spring.datasource.url", parts[2]);
            }
            log.info("Secreto parseado en formato simple");
        } else {
            log.warn("Formato de secreto no reconocido. Longitud: {}", parts.length);
        }
    }
    
    /**
     * Extrae la contraseña del secreto passwirk_sec
     * Puede ser un string simple (solo la contraseña) o un JSON con {"password": "..."}
     */
    private String extractPasswordFromSecret(String secretValue) {
        try {
            // Intentar parsear como JSON
            JsonNode jsonNode = objectMapper.readTree(secretValue);
            String password = getJsonValue(jsonNode, "password", "pass", "db_password", "value");
            if (password != null) {
                return password;
            }
        } catch (Exception e) {
            // No es JSON, tratar como string simple (solo la contraseña)
            log.debug("El secreto de contraseña no es JSON, tratando como string simple");
        }
        
        // Si no es JSON o no tiene campo password, usar el valor completo como contraseña
        return secretValue.trim();
    }
}

