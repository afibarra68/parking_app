# Configuración de Cloud SQL con Unix Socket

## Descripción

La aplicación está configurada para usar el socket Unix estándar de Cloud SQL cuando se ejecuta en Cloud Run. Cloud Run monta automáticamente el socket en `/cloudsql/PROJECT-ID:REGION:INSTANCE-ID` cuando se usa `--add-cloudsql-instances`.

## Cómo Funciona

### En Cloud Run

1. **Cloud Run monta el socket automáticamente** cuando usas `--add-cloudsql-instances`
2. El socket se monta en: `/cloudsql/PROJECT-ID:REGION:INSTANCE-ID`
3. La aplicación detecta automáticamente el socket y lo usa para la conexión

### Variables de Entorno

Cloud Run configura automáticamente:
- `DB_SOCKET=/cloudsql/PROJECT-ID:REGION:INSTANCE-ID` (ruta del socket Unix)
- `CLOUD_SQL_INSTANCE=PROJECT-ID:REGION:INSTANCE-ID` (nombre de la instancia)

### Formato de Conexión JDBC

La aplicación usa Cloud SQL Socket Factory que maneja automáticamente el socket:

```java
jdbc:postgresql:///<DATABASE_NAME>?cloudSqlInstance=PROJECT-ID:REGION:INSTANCE-ID&socketFactory=com.google.cloud.sql.postgres.SocketFactory
```

## Configuración en cloudbuild.yaml

El `cloudbuild.yaml` ya está configurado para:

1. Agregar la instancia de Cloud SQL: `--add-cloudsql-instances`
2. Configurar variables de entorno: `DB_SOCKET` y `CLOUD_SQL_INSTANCE`

```yaml
- '--set-env-vars'
- 'SPRING_PROFILES_ACTIVE=prod,DB_SOCKET=/cloudsql/${_CLOUD_SQL_INSTANCE},CLOUD_SQL_INSTANCE=${_CLOUD_SQL_INSTANCE}'
- '--add-cloudsql-instances'
- '${_CLOUD_SQL_INSTANCE}'
```

## Orden de Prioridad para Construir la URL

La aplicación construye la URL de conexión en el siguiente orden:

1. **SPRING_DATASOURCE_URL** (variable de entorno o secreto) - Mayor prioridad
2. **cloudSqlInstance del secreto postgres_auth**
3. **CLOUD_SQL_INSTANCE** (variable de entorno)
4. **DB_SOCKET** (extrae instancia del path `/cloudsql/PROJECT-ID:REGION:INSTANCE-ID`)
5. **host/private_ip del secreto** (fallback)

## Ejemplo de Configuración

### En cloudbuild.yaml

```yaml
substitutions:
  _CLOUD_SQL_INSTANCE: '649345514504:us-central1:parking-db-instance'
```

### Variables de Entorno Resultantes

```bash
DB_SOCKET=/cloudsql/649345514504:us-central1:parking-db-instance
CLOUD_SQL_INSTANCE=649345514504:us-central1:parking-db-instance
```

### URL JDBC Construida

```
jdbc:postgresql:///mp_parking_app?cloudSqlInstance=649345514504:us-central1:parking-db-instance&socketFactory=com.google.cloud.sql.postgres.SocketFactory
```

## Configuración Manual en Cloud Run

Si despliegas manualmente:

```bash
gcloud run deploy parking-backend \
  --image gcr.io/649345514504/parking-backend:latest \
  --region us-central1 \
  --add-cloudsql-instances 649345514504:us-central1:parking-db-instance \
  --set-env-vars \
    "SPRING_PROFILES_ACTIVE=prod,DB_SOCKET=/cloudsql/649345514504:us-central1:parking-db-instance,CLOUD_SQL_INSTANCE=649345514504:us-central1:parking-db-instance"
```

## Verificación

### Verificar que el socket está montado

```bash
# Desde Cloud Run (usando Cloud Shell conectado al servicio)
gcloud run services describe parking-backend --region us-central1

# Verificar en los logs
gcloud run services logs read parking-backend --region us-central1 | grep -i "cloudsql\|socket"
```

### Verificar conexión

La aplicación debería conectarse automáticamente usando el socket. Revisa los logs para confirmar:

```bash
gcloud run services logs read parking-backend --region us-central1 | grep -i "datasource\|connection"
```

## Ventajas del Socket Unix

1. **Mejor rendimiento**: Conexión directa sin overhead de red TCP
2. **Más seguro**: No expone puertos a Internet
3. **Automático**: Cloud Run lo monta automáticamente
4. **Sin configuración adicional**: Solo necesitas `--add-cloudsql-instances`

## Troubleshooting

### Error: Socket no encontrado

- Verificar que `--add-cloudsql-instances` está configurado correctamente
- Verificar que el formato es `PROJECT-ID:REGION:INSTANCE-ID`
- Verificar que la instancia de Cloud SQL existe y está activa

### Error: Permission denied

- Verificar que el service account tiene rol `cloudsql.client`
- Verificar que Cloud Run tiene permisos para acceder a Cloud SQL

### Error: Connection timeout

- Verificar que la instancia de Cloud SQL está en la misma región (recomendado)
- Verificar que la instancia está activa y no en estado SUSPENDED
- Revisar logs de Cloud SQL para ver intentos de conexión

## Referencias

- [Cloud SQL Socket Factory](https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory)
- [Cloud Run con Cloud SQL](https://cloud.google.com/run/docs/connect/cloud-sql)
- [Unix Socket en Cloud Run](https://cloud.google.com/sql/docs/postgres/connect-run)

