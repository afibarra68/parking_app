# Troubleshooting PostgreSQL Connection Error

## Error: "Something unusual has occurred to cause the driver to fail"

Este error genérico de PostgreSQL puede tener varias causas. Aquí están las soluciones más comunes:

## Posibles Causas y Soluciones

### 1. Versión del Driver de PostgreSQL

✅ **Solución aplicada**: Actualizado a versión `42.7.3` que es compatible con:
- Spring Boot 3.5.0
- Cloud SQL Socket Factory 1.14.1
- PostgreSQL 15

### 2. Formato de URL JDBC Incorrecto

**Para Cloud SQL con Socket Factory:**
```
jdbc:postgresql:///<DATABASE_NAME>?cloudSqlInstance=PROJECT_ID:REGION:INSTANCE_NAME&socketFactory=com.google.cloud.sql.postgres.SocketFactory
```

**Importante:**
- Usar `///` (tres barras) después de `postgresql:`
- No incluir `host:puerto` en la URL
- El nombre de la base de datos debe ser el correcto

### 3. Base de Datos No Existe

Verificar que la base de datos existe en Cloud SQL:

```bash
gcloud sql databases list --instance=parkingprodgcloud --project=testauth20-394320
```

Si no existe, crearla:

```bash
gcloud sql databases create mp_parking_app --instance=parkingprodgcloud --project=testauth20-394320
```

### 4. Credenciales Incorrectas

Verificar que:
- El usuario existe en Cloud SQL
- La contraseña es correcta (desde Secret Manager `passwirk_sec`)
- El usuario tiene permisos en la base de datos

### 5. Socket Unix No Disponible

En Cloud Run, verificar que:
- `--add-cloudsql-instances` está configurado correctamente
- El socket se monta en `/cloudsql/testauth20-394320:us-central1:parkingprodgcloud`
- El service account tiene permisos `cloudsql.client`

### 6. Timeout de Conexión

Si la conexión tarda mucho:
- Aumentar `connection-timeout` en HikariCP
- Verificar que la instancia de Cloud SQL esté activa
- Verificar la latencia de red

## Configuración Actual

### Driver PostgreSQL
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
</dependency>
```

### URL de Conexión
```yaml
url: jdbc:postgresql:///mp_parking_app?cloudSqlInstance=testauth20-394320:us-central1:parkingprodgcloud&socketFactory=com.google.cloud.sql.postgres.SocketFactory
```

### Logging Habilitado
```yaml
logging:
  level:
    org.postgresql: DEBUG
    com.zaxxer.hikari: DEBUG
    com.google.cloud.sql: DEBUG
```

## Pasos de Diagnóstico

### 1. Verificar Logs Detallados

Con el logging en DEBUG, deberías ver:
- Intentos de conexión
- Errores específicos del driver
- Información del socket factory

### 2. Verificar Base de Datos

```bash
# Listar bases de datos
gcloud sql databases list --instance=parkingprodgcloud --project=testauth20-394320

# Verificar usuarios
gcloud sql users list --instance=parkingprodgcloud --project=testauth20-394320
```

### 3. Probar Conexión Manualmente

```bash
# Conectar usando Cloud SQL Proxy
cloud-sql-proxy testauth20-394320:us-central1:parkingprodgcloud

# En otra terminal
psql -h 127.0.0.1 -U postgres -d mp_parking_app
```

### 4. Verificar Secretos

```bash
# Verificar que el secreto existe
gcloud secrets describe passwirk_sec --project=testauth20-394320

# Ver el valor (cuidado con la seguridad)
gcloud secrets versions access latest --secret=passwirk_sec --project=testauth20-394320
```

## Soluciones Adicionales

### Si el error persiste, intentar:

1. **Usar IP privada directamente** (si VPC está configurado):
```yaml
url: jdbc:postgresql://10.x.x.x:5432/mp_parking_app
```

2. **Verificar permisos del service account**:
```bash
gcloud projects get-iam-policy testauth20-394320 \
  --flatten="bindings[].members" \
  --filter="bindings.members:serviceAccount:*"
```

3. **Aumentar timeout de conexión**:
```yaml
hikari:
  connection-timeout: 60000  # 60 segundos
```

4. **Verificar que la instancia esté activa**:
```bash
gcloud sql instances describe parkingprodgcloud --project=testauth20-394320
```

## Referencias

- [Cloud SQL Socket Factory](https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/)
- [Spring Boot Data Source Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.datasource)

