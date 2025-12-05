#!/bin/bash
# Script para cargar secretos desde Google Cloud Secret Manager
# Uso: source scripts/load-secrets.sh

set -e

# Variables requeridas
PROJECT_ID=${GCP_PROJECT_ID:-""}
SECRET_PREFIX=${SECRET_PREFIX:-"parking-app"}

if [ -z "$PROJECT_ID" ]; then
    echo "Error: GCP_PROJECT_ID no está configurado"
    exit 1
fi

echo "Cargando secretos desde Google Cloud Secret Manager para proyecto: $PROJECT_ID"

# Función para obtener un secreto
get_secret() {
    local secret_name=$1
    local env_var=$2
    
    if gcloud secrets versions access latest --secret="${SECRET_PREFIX}-${secret_name}" --project="${PROJECT_ID}" > /dev/null 2>&1; then
        export "${env_var}=$(gcloud secrets versions access latest --secret="${SECRET_PREFIX}-${secret_name}" --project="${PROJECT_ID}")"
        echo "✓ Secreto ${secret_name} cargado"
    else
        echo "⚠ Advertencia: Secreto ${secret_name} no encontrado"
    fi
}

# Cargar secretos de la base de datos
get_secret "db-url" "SPRING_DATASOURCE_URL"
get_secret "db-username" "SPRING_DATASOURCE_USERNAME"
get_secret "db-password" "SPRING_DATASOURCE_PASSWORD"
get_secret "db-name" "SPRING_DATASOURCE_DB_NAME"

# Cargar secretos de JWT
get_secret "jwt-secret" "SPRING_JWT_SECRET"
get_secret "jwt-expiration" "SPRING_JWT_EXPIRATION"

# Cargar configuración de Cloud SQL
get_secret "cloud-sql-instance" "CLOUD_SQL_INSTANCE"

# Si tenemos el nombre de la instancia, construir la URL de Cloud SQL
if [ -n "$CLOUD_SQL_INSTANCE" ] && [ -n "$SPRING_DATASOURCE_DB_NAME" ]; then
    export SPRING_DATASOURCE_URL="jdbc:postgresql:///${SPRING_DATASOURCE_DB_NAME}?cloudSqlInstance=${CLOUD_SQL_INSTANCE}&socketFactory=com.google.cloud.sql.postgres.SocketFactory"
    echo "✓ URL de Cloud SQL configurada"
fi

echo "Secretos cargados exitosamente"

