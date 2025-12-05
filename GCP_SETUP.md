# Configuración de Google Cloud para Parking App

Esta guía explica cómo configurar la aplicación para usar Google Cloud Secret Manager y Cloud SQL de forma segura.

## Prerrequisitos

1. Tener una cuenta de Google Cloud con un proyecto creado
2. Tener `gcloud` CLI instalado y configurado
3. Tener permisos de administrador en el proyecto

## Paso 1: Habilitar APIs necesarias

```bash
# Configurar el proyecto
export GCP_PROJECT_ID=tu-proyecto-id
gcloud config set project $GCP_PROJECT_ID

# Habilitar APIs necesarias
gcloud services enable secretmanager.googleapis.com
gcloud services enable sqladmin.googleapis.com
gcloud services enable run.googleapis.com
gcloud services enable cloudbuild.googleapis.com
```

## Paso 2: Crear instancia de Cloud SQL

```bash
# Crear instancia de PostgreSQL
gcloud sql instances create parking-db-instance \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1 \
  --root-password=TU_PASSWORD_SEGURO

# Crear base de datos
gcloud sql databases create db_parking \
  --instance=parking-db-instance

# Crear usuario
gcloud sql users create parking_user \
  --instance=parking-db-instance \
  --password=TU_PASSWORD_SEGURO
```

## Paso 3: Configurar Secret Manager

### Opción A: Usar el script automatizado

```bash
# Dar permisos de ejecución
chmod +x scripts/setup-secrets.sh

# Configurar variables
export GCP_PROJECT_ID=tu-proyecto-id
export SECRET_PREFIX=parking-app

# Ejecutar script
./scripts/setup-secrets.sh
```

### Opción B: Crear secretos manualmente

```bash
# URL de conexión a Cloud SQL
# Formato: jdbc:postgresql:///<DB_NAME>?cloudSqlInstance=<PROJECT_ID>:<REGION>:<INSTANCE_NAME>&socketFactory=com.google.cloud.sql.postgres.SocketFactory
echo -n "jdbc:postgresql:///db_parking?cloudSqlInstance=${GCP_PROJECT_ID}:us-central1:parking-db-instance&socketFactory=com.google.cloud.sql.postgres.SocketFactory" | \
  gcloud secrets create parking-app-db-url \
    --data-file=- \
    --replication-policy="automatic"

# Usuario de la base de datos
echo -n "parking_user" | \
  gcloud secrets create parking-app-db-username \
    --data-file=- \
    --replication-policy="automatic"

# Contraseña de la base de datos
echo -n "TU_PASSWORD_SEGURO" | \
  gcloud secrets create parking-app-db-password \
    --data-file=- \
    --replication-policy="automatic"

# JWT Secret
echo -n "TU_JWT_SECRET_KEY" | \
  gcloud secrets create parking-app-jwt-secret \
    --data-file=- \
    --replication-policy="automatic"
```

## Paso 4: Configurar Service Account para Cloud Run

```bash
# Crear service account
gcloud iam service-accounts create parking-backend-sa \
  --display-name="Parking Backend Service Account"

# Otorgar permisos para acceder a Secret Manager
gcloud secrets add-iam-policy-binding parking-app-db-url \
  --member="serviceAccount:parking-backend-sa@${GCP_PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding parking-app-db-username \
  --member="serviceAccount:parking-backend-sa@${GCP_PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding parking-app-db-password \
  --member="serviceAccount:parking-backend-sa@${GCP_PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding parking-app-jwt-secret \
  --member="serviceAccount:parking-backend-sa@${GCP_PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"

# Otorgar permisos para conectar a Cloud SQL
gcloud projects add-iam-policy-binding ${GCP_PROJECT_ID} \
  --member="serviceAccount:parking-backend-sa@${GCP_PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/cloudsql.client"
```

## Paso 5: Desplegar en Cloud Run

### Opción A: Usar Cloud Build (Recomendado)

```bash
# Configurar variables de sustitución
gcloud builds submit --config=cloudbuild.yaml \
  --substitutions=_CLOUD_SQL_INSTANCE=${GCP_PROJECT_ID}:us-central1:parking-db-instance
```

### Opción B: Desplegar manualmente

```bash
# Construir imagen
docker build -f Dockerfile.cloud -t gcr.io/${GCP_PROJECT_ID}/parking-backend:latest .

# Push a Container Registry
docker push gcr.io/${GCP_PROJECT_ID}/parking-backend:latest

# Desplegar en Cloud Run
gcloud run deploy parking-backend \
  --image gcr.io/${GCP_PROJECT_ID}/parking-backend:latest \
  --platform managed \
  --region us-central1 \
  --service-account parking-backend-sa@${GCP_PROJECT_ID}.iam.gserviceaccount.com \
  --add-cloudsql-instances ${GCP_PROJECT_ID}:us-central1:parking-db-instance \
  --set-env-vars SPRING_PROFILES_ACTIVE=prod \
  --set-secrets SPRING_DATASOURCE_URL=parking-app-db-url:latest,SPRING_DATASOURCE_USERNAME=parking-app-db-username:latest,SPRING_DATASOURCE_PASSWORD=parking-app-db-password:latest,SPRING_JWT_SECRET=parking-app-jwt-secret:latest \
  --allow-unauthenticated
```

## Paso 6: Configurar para desarrollo local

Para desarrollo local, puedes usar el script para cargar secretos:

```bash
# Configurar variables
export GCP_PROJECT_ID=tu-proyecto-id
export SECRET_PREFIX=parking-app

# Cargar secretos
source scripts/load-secrets.sh

# O crear un archivo .env basado en .env.example
cp .env.example .env
# Editar .env con tus valores locales o de desarrollo
```

## Verificación

1. Verificar que los secretos existen:
```bash
gcloud secrets list --filter="name:parking-app-*"
```

2. Verificar que Cloud Run tiene acceso:
```bash
gcloud run services describe parking-backend --region us-central1
```

3. Probar la conexión:
```bash
curl https://parking-backend-XXXXX.run.app/actuator/health
```

## Actualizar secretos

Para actualizar un secreto:

```bash
echo -n "nuevo-valor" | \
  gcloud secrets versions add parking-app-db-password \
    --data-file=-
```

## Troubleshooting

### Error: Permission denied
- Verificar que el service account tiene los permisos correctos
- Verificar que los secretos existen y tienen versiones

### Error: Cloud SQL connection failed
- Verificar que la instancia de Cloud SQL está activa
- Verificar que el service account tiene rol `cloudsql.client`
- Verificar que `--add-cloudsql-instances` está configurado correctamente

### Error: Secret not found
- Verificar que los nombres de los secretos coinciden
- Verificar que el prefijo `SECRET_PREFIX` es correcto

