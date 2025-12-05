# Configuración de Google Cloud Secret Manager

## Descripción

La aplicación está configurada para leer automáticamente las credenciales de la base de datos desde Google Cloud Secret Manager cuando se ejecuta con el perfil `prod`.

## Secretos Configurados

### Secreto Principal: postgres_auth
- **Proyecto**: `649345514504`
- **Nombre del secreto**: `postgres_auth`
- **Ruta completa**: `projects/649345514504/secrets/postgres_auth`
- **Contenido**: Configuración completa de la base de datos (URL, username, host, etc.)

### Secreto de Contraseña: passwirk_sec
- **Proyecto**: `649345514504`
- **Nombre del secreto**: `passwirk_sec`
- **Ruta completa**: `projects/649345514504/secrets/passwirk_sec`
- **Contenido**: Contraseña de la base de datos (tiene prioridad sobre password en postgres_auth)
- **Formato**: Puede ser un string simple (solo la contraseña) o JSON: `{"password": "tu-password"}`

## Formato del Secreto

El secreto `postgres_auth` puede estar en dos formatos:

### Formato JSON (Recomendado)

```json
{
  "username": "postgres",
  "password": "tu-password",
  "url": "jdbc:postgresql:///db_name?cloudSqlInstance=proyecto:region:instancia&socketFactory=com.google.cloud.sql.postgres.SocketFactory",
  "database": "db_name",
  "cloud_sql_instance": "proyecto:region:instancia"
}
```

Para usar IP privada de VPC:

```json
{
  "username": "postgres",
  "password": "tu-password",
  "private_ip": "10.x.x.x",
  "database": "db_name",
  "port": "5432"
}
```

O con URL completa usando IP privada:

```json
{
  "username": "postgres",
  "password": "tu-password",
  "url": "jdbc:postgresql://10.x.x.x:5432/db_name",
  "database": "db_name"
}
```

O también puede contener formato estándar:

```json
{
  "username": "postgres",
  "password": "tu-password",
  "host": "localhost",
  "port": "5432",
  "database": "db_name"
}
```

### Formato Simple

```
username:password:jdbc_url
```

## Cómo Funciona

1. Cuando la aplicación inicia con el perfil `prod`, la clase `SecretManagerConfig` se ejecuta automáticamente
2. Lee el secreto `postgres_auth` desde Google Cloud Secret Manager (configuración completa)
3. Lee el secreto `passwirk_sec` desde Google Cloud Secret Manager (contraseña)
4. La contraseña de `passwirk_sec` tiene **prioridad** sobre la contraseña en `postgres_auth`
5. Parsea el contenido (JSON o formato simple)
6. Configura las propiedades de Spring Boot antes de que se inicialice el datasource
7. La aplicación usa estas credenciales para conectarse a la base de datos

## Permisos Requeridos

El servicio que ejecuta la aplicación necesita:

1. **Service Account** con el rol:
   - `roles/secretmanager.secretAccessor`

2. **Para Cloud Run**, el service account debe tener acceso a ambos secretos:
```bash
# Acceso a postgres_auth
gcloud secrets add-iam-policy-binding postgres_auth \
  --member="serviceAccount:tu-service-account@649345514504.iam.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor" \
  --project="649345514504"

# Acceso a passwirk_sec
gcloud secrets add-iam-policy-binding passwirk_sec \
  --member="serviceAccount:tu-service-account@649345514504.iam.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor" \
  --project="649345514504"
```

## Configuración

### Habilitar/Deshabilitar Secret Manager

En `application-prod.yml` o como variable de entorno:

```yaml
gcp:
  secret-manager:
    enabled: true  # o false para deshabilitar
```

O como variable de entorno:
```bash
export GCP_SECRET_MANAGER_ENABLED=true
```

### Variables de Entorno Alternativas

Si Secret Manager no está disponible o está deshabilitado, la aplicación usará variables de entorno estándar:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Desarrollo Local

Para desarrollo local, puedes:

1. **Deshabilitar Secret Manager**:
```bash
export GCP_SECRET_MANAGER_ENABLED=false
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/db_parking_local
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=tu-password
```

2. **Usar credenciales de GCP localmente**:
```bash
# Autenticarse con gcloud
gcloud auth application-default login

# La aplicación leerá automáticamente el secreto
export SPRING_PROFILES_ACTIVE=prod
```

## Actualizar los Secretos

### Actualizar postgres_auth (configuración completa)

```bash
# Actualizar secreto (formato JSON)
echo -n '{"username":"postgres","url":"jdbc:postgresql://...","database":"db_name"}' | \
  gcloud secrets versions add postgres_auth \
    --data-file=- \
    --project=649345514504

# O desde un archivo
gcloud secrets versions add postgres_auth \
  --data-file=secret.json \
  --project=649345514504
```

### Actualizar passwirk_sec (contraseña)

```bash
# Actualizar contraseña (string simple)
echo -n "tu-nueva-contraseña" | \
  gcloud secrets versions add passwirk_sec \
    --data-file=- \
    --project=649345514504

# O como JSON
echo -n '{"password":"tu-nueva-contraseña"}' | \
  gcloud secrets versions add passwirk_sec \
    --data-file=- \
    --project=649345514504
```

**Nota**: La contraseña en `passwirk_sec` tiene prioridad sobre cualquier contraseña en `postgres_auth`.

## Troubleshooting

### Error: Permission denied
- Verificar que el service account tiene el rol `secretmanager.secretAccessor`
- Verificar que el secreto existe: `gcloud secrets describe postgres_auth --project=649345514504`

### Error: Secret not found
- Verificar los nombres de los secretos: `postgres_auth` y `passwirk_sec`
- Verificar el proyecto: `649345514504`
- Listar secretos: `gcloud secrets list --project=649345514504`
- Verificar que ambos secretos existen y tienen versiones

### Error: Cannot parse secret
- Verificar que el formato del secreto sea JSON válido o formato simple
- Revisar los logs de la aplicación para ver el error específico

### La aplicación no carga el secreto
- Verificar que el perfil activo es `prod`: `SPRING_PROFILES_ACTIVE=prod`
- Verificar que `gcp.secret-manager.enabled=true`
- Revisar los logs de inicio de la aplicación

## Logs

La aplicación registra información sobre la carga de secretos:

- `INFO`: Secreto cargado exitosamente
- `WARN`: Advertencias sobre formato o valores faltantes
- `ERROR`: Errores al acceder a Secret Manager
- `DEBUG`: Detalles de las propiedades configuradas (sin mostrar passwords)

