#!/bin/bash
# Script para obtener información de VPC y direcciones IP
# Uso: ./scripts/get-vpc-info.sh

set -e

PROJECT_ID=${GCP_PROJECT_ID:-"649345514504"}
REGION=${GCP_REGION:-"us-central1"}
VPC_CONNECTOR_NAME=${VPC_CONNECTOR_NAME:-"parking-vpc-connector"}
CLOUD_SQL_INSTANCE=${CLOUD_SQL_INSTANCE:-""}

echo "Información de VPC para proyecto: $PROJECT_ID"
echo "=========================================="
echo ""

# Información del VPC Connector
echo "VPC Connector:"
if gcloud compute networks vpc-access connectors describe $VPC_CONNECTOR_NAME \
    --region=$REGION \
    --project=$PROJECT_ID &>/dev/null; then
    
    gcloud compute networks vpc-access connectors describe $VPC_CONNECTOR_NAME \
        --region=$REGION \
        --project=$PROJECT_ID \
        --format="table(name,network,ipCidrRange,state)"
else
    echo "VPC Connector no encontrado"
fi

echo ""

# Información de Cloud SQL si se proporciona
if [ -n "$CLOUD_SQL_INSTANCE" ]; then
    echo "Cloud SQL Instance: $CLOUD_SQL_INSTANCE"
    echo "Direcciones IP:"
    gcloud sql instances describe $CLOUD_SQL_INSTANCE \
        --project=$PROJECT_ID \
        --format="table(ipAddresses[].type,ipAddresses[].ipAddress)" || echo "Instancia no encontrada"
    echo ""
    
    # Obtener IP privada
    PRIVATE_IP=$(gcloud sql instances describe $CLOUD_SQL_INSTANCE \
        --project=$PROJECT_ID \
        --format="value(ipAddresses[?type=='PRIVATE'].ipAddress)" 2>/dev/null || echo "")
    
    if [ -n "$PRIVATE_IP" ]; then
        echo "IP Privada: $PRIVATE_IP"
        echo ""
        echo "URL de conexión JDBC con IP privada:"
        echo "jdbc:postgresql://$PRIVATE_IP:5432/<DATABASE_NAME>"
    else
        echo "No se encontró IP privada configurada"
    fi
fi

