# Configuración de Docker y Archivos YML

## Estructura de Configuración

La aplicación está configurada para usar el archivo `application-prod.yml` en producción a través de Docker.

### Rutas de Configuración

1. **Dentro del JAR**: Los archivos YML en `src/main/resources/` se empaquetan automáticamente en el JAR
2. **Archivo externo**: El Dockerfile copia `application-prod.yml` a `/app/config/` para permitir override sin reconstruir

### Orden de Prioridad de Configuración

Spring Boot carga la configuración en el siguiente orden (mayor a menor prioridad):

1. **Variables de entorno** (más alta prioridad)
2. **Archivos externos** (`/app/config/application-prod.yml`)
3. **Archivos dentro del JAR** (`application-prod.yml` empaquetado)
4. **application.yml** (por defecto)

## Dockerfile

### Dockerfile (Desarrollo/Producción Local)

```dockerfile
# Copia el application-prod.yml a /app/config/
COPY --from=build /app/src/main/resources/application-prod.yml ./config/application-prod.yml

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_CONFIG_ADDITIONAL_LOCATION=file:/app/config/

# Ejecuta con perfil prod y ruta adicional
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=prod --spring.config.additional-location=file:/app/config/"]
```

### Dockerfile.cloud (Google Cloud Run)

Similar al Dockerfile pero optimizado para Cloud Run:
- Usa puerto dinámico (PORT variable)
- Configura variables de GCP por defecto
- Usuario no-root para seguridad

## Variables de Entorno Importantes

### Para Producción

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_CONFIG_ADDITIONAL_LOCATION=file:/app/config/
GCP_PROJECT_ID=649345514504
GCP_SECRET_MANAGER_ENABLED=true
```

### Para Desarrollo Local

```bash
SPRING_PROFILES_ACTIVE=dev
# O simplemente no definir SPRING_PROFILES_ACTIVE para usar application.yml
```

## Uso con Docker Compose

El `docker-compose.yml` está configurado para:

1. Leer variables desde `.env`
2. Activar perfil `prod` por defecto
3. Usar ruta adicional de configuración
4. Habilitar Secret Manager de GCP

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}
  - SPRING_CONFIG_ADDITIONAL_LOCATION=file:/app/config/
  - GCP_PROJECT_ID=${GCP_PROJECT_ID:-649345514504}
  - GCP_SECRET_MANAGER_ENABLED=${GCP_SECRET_MANAGER_ENABLED:-true}
```

## Override de Configuración

### Opción 1: Variables de Entorno

```bash
docker run -e SPRING_DATASOURCE_URL="jdbc:postgresql://..." \
  -e SPRING_DATASOURCE_USERNAME="user" \
  -e SPRING_DATASOURCE_PASSWORD="pass" \
  parking-backend
```

### Opción 2: Montar Archivo de Configuración

```bash
docker run -v /ruta/local/application-prod.yml:/app/config/application-prod.yml \
  parking-backend
```

### Opción 3: Modificar en el Contenedor

```bash
# Entrar al contenedor
docker exec -it parking-backend sh

# Editar configuración
vi /app/config/application-prod.yml

# Reiniciar (si es necesario)
```

## Verificación

Para verificar qué configuración está usando la aplicación:

```bash
# Ver logs de inicio
docker logs parking-backend | grep "The following profiles are active"

# O desde dentro del contenedor
docker exec parking-backend sh -c "ls -la /app/config/"
```

## Troubleshooting

### La aplicación no usa application-prod.yml

1. Verificar que `SPRING_PROFILES_ACTIVE=prod` está configurado
2. Verificar que el archivo existe en `/app/config/application-prod.yml`
3. Revisar logs para ver qué perfiles están activos

### Error: Cannot find application-prod.yml

- El archivo está dentro del JAR, no debería dar error
- Si usas ruta externa, verificar que el archivo existe en `/app/config/`
- Verificar permisos del directorio `/app/config/`

### Configuración no se aplica

- Verificar orden de prioridad (variables de entorno > archivos externos > JAR)
- Verificar que no hay errores de sintaxis en el YML
- Revisar logs de Spring Boot para ver qué configuración se está cargando

## Mejores Prácticas

1. **No hardcodear valores sensibles** en `application-prod.yml`
2. **Usar variables de entorno** para valores que cambian entre entornos
3. **Usar Secret Manager** para credenciales en producción
4. **Mantener `application-prod.yml`** en el repositorio sin valores sensibles
5. **Documentar** todas las variables de entorno necesarias

