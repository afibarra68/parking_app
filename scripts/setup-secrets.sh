#!/bin/bash
# Script para crear secretos en Google Cloud Secret Manager
# Uso: ./scripts/setup-secrets.sh

set -e

PROJECT_ID=${GCP_PROJECT_ID:-""}
SECRET_PREFIX=${SECRET_PREFIX:-"parking-app"}

if [ -z "$PROJECT_ID" ]; then
    echo "Error: GCP_PROJECT_ID no está configurado"
    echo "Ejecuta: export GCP_PROJECT_ID=tu-proyecto-id"
    exit 1
fi

echo "Configurando secretos en Google Cloud Secret Manager para proyecto: $PROJECT_ID"
echo "Prefijo de secretos: $SECRET_PREFIX"
echo ""

# Función para crear o actualizar un secreto
create_secret() {
    local secret_name=$1
    local description=$2
    local full_name="${SECRET_PREFIX}-${secret_name}"
    
    if gcloud secrets describe "$full_name" --project="$PROJECT_ID" > /dev/null 2>&1; then
        echo "El secreto $full_name ya existe. ¿Deseas actualizarlo? (s/n)"
        read -r response
        if [[ "$response" =~ ^[Ss]$ ]]; then
            echo "Ingresa el valor para $full_name:"
            read -s secret_value
            echo -n "$secret_value" | gcloud secrets versions add "$full_name" --project="$PROJECT_ID" --data-file=-
            echo "✓ Secreto $full_name actualizado"
        fi
    else
        echo "Creando secreto $full_name..."
        echo "$description" | gcloud secrets create "$full_name" \
            --project="$PROJECT_ID" \
            --replication-policy="automatic" \
            --data-file=- || true
        
        echo "Ingresa el valor para $full_name:"
        read -s secret_value
        echo -n "$secret_value" | gcloud secrets versions add "$full_name" --project="$PROJECT_ID" --data-file=-
        echo "✓ Secreto $full_name creado"
    fi
    echo ""
}

# Crear secretos necesarios
create_secret "db-url" "URL de conexión a la base de datos"
create_secret "db-username" "Usuario de la base de datos"
create_secret "db-password" "Contraseña de la base de datos"
create_secret "db-name" "Nombre de la base de datos"
create_secret "cloud-sql-instance" "Nombre de la instancia de Cloud SQL (formato: proyecto:región:instancia)"
create_secret "jwt-secret" "Secret key para JWT"
create_secret "jwt-expiration" "Tiempo de expiración de JWT en milisegundos"

echo "¡Configuración completada!"
echo ""
echo "Para otorgar acceso a un servicio, ejecuta:"
echo "gcloud secrets add-iam-policy-binding ${SECRET_PREFIX}-<nombre-secreto> \\"
echo "  --member='serviceAccount:SERVICE_ACCOUNT@${PROJECT_ID}.iam.gserviceaccount.com' \\"
echo "  --role='roles/secretmanager.secretAccessor' \\"
echo "  --project='${PROJECT_ID}'"

