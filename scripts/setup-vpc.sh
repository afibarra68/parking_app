#!/bin/bash
# Script para configurar VPC y VPC Connector para Cloud Run y Cloud SQL
# Uso: ./scripts/setup-vpc.sh

set -e

# Variables de configuración
PROJECT_ID=${GCP_PROJECT_ID:-"649345514504"}
REGION=${GCP_REGION:-"us-central1"}
VPC_NETWORK_NAME=${VPC_NETWORK_NAME:-"parking-vpc"}
VPC_CONNECTOR_NAME=${VPC_CONNECTOR_NAME:-"parking-vpc-connector"}
CLOUD_SQL_INSTANCE=${CLOUD_SQL_INSTANCE:-""}

echo "Configurando VPC para proyecto: $PROJECT_ID"
echo "Región: $REGION"
echo ""

# Configurar proyecto
gcloud config set project $PROJECT_ID

# Habilitar APIs necesarias
echo "Habilitando APIs necesarias..."
gcloud services enable \
    compute.googleapis.com \
    vpcaccess.googleapis.com \
    servicenetworking.googleapis.com \
    sqladmin.googleapis.com

# Crear VPC Network si no existe
echo ""
echo "Verificando/Creando VPC Network: $VPC_NETWORK_NAME"
if ! gcloud compute networks describe $VPC_NETWORK_NAME --project=$PROJECT_ID &>/dev/null; then
    echo "Creando VPC Network: $VPC_NETWORK_NAME"
    gcloud compute networks create $VPC_NETWORK_NAME \
        --project=$PROJECT_ID \
        --subnet-mode=auto \
        --bgp-routing-mode=regional \
        --mtu=1460
    echo "✓ VPC Network creada"
else
    echo "✓ VPC Network ya existe"
fi

# Crear VPC Connector para Cloud Run
echo ""
echo "Verificando/Creando VPC Connector: $VPC_CONNECTOR_NAME"
if ! gcloud compute networks vpc-access connectors describe $VPC_CONNECTOR_NAME \
    --region=$REGION \
    --project=$PROJECT_ID &>/dev/null; then
    
    echo "Creando VPC Connector: $VPC_CONNECTOR_NAME"
    gcloud compute networks vpc-access connectors create $VPC_CONNECTOR_NAME \
        --region=$REGION \
        --network=$VPC_NETWORK_NAME \
        --range=10.8.0.0/28 \
        --min-instances=2 \
        --max-instances=3 \
        --machine-type=e2-micro \
        --project=$PROJECT_ID
    
    echo "✓ VPC Connector creado"
else
    echo "✓ VPC Connector ya existe"
fi

# Configurar Cloud SQL con Private IP si se proporciona la instancia
if [ -n "$CLOUD_SQL_INSTANCE" ]; then
    echo ""
    echo "Configurando Cloud SQL con Private IP para: $CLOUD_SQL_INSTANCE"
    
    # Verificar si ya tiene IP privada
    PRIVATE_IP=$(gcloud sql instances describe $CLOUD_SQL_INSTANCE \
        --project=$PROJECT_ID \
        --format="value(ipAddresses[0].ipAddress)" 2>/dev/null || echo "")
    
    if [ -z "$PRIVATE_IP" ] || [[ ! "$PRIVATE_IP" =~ ^10\. ]]; then
        echo "Asignando Private IP a Cloud SQL..."
        
        # Reservar rango de IP para Cloud SQL
        echo "Reservando rango de IP para Cloud SQL..."
        gcloud compute addresses create google-managed-services-$VPC_NETWORK_NAME \
            --global \
            --purpose=VPC_PEERING \
            --prefix-length=16 \
            --network=projects/$PROJECT_ID/global/networks/$VPC_NETWORK_NAME \
            --project=$PROJECT_ID 2>/dev/null || echo "Rango de IP ya existe"
        
        # Conectar servicio de red privada
        echo "Conectando servicio de red privada..."
        gcloud services vpc-peerings connect \
            --service=servicenetworking.googleapis.com \
            --ranges=google-managed-services-$VPC_NETWORK_NAME \
            --network=$VPC_NETWORK_NAME \
            --project=$PROJECT_ID 2>/dev/null || echo "Conexión ya existe"
        
        # Asignar IP privada a Cloud SQL
        echo "Asignando IP privada a Cloud SQL instance..."
        gcloud sql instances patch $CLOUD_SQL_INSTANCE \
            --network=projects/$PROJECT_ID/global/networks/$VPC_NETWORK_NAME \
            --no-assign-ip \
            --project=$PROJECT_ID
        
        echo "✓ Cloud SQL configurado con Private IP"
    else
        echo "✓ Cloud SQL ya tiene IP privada: $PRIVATE_IP"
    fi
fi

# Mostrar información
echo ""
echo "=========================================="
echo "Configuración VPC completada"
echo "=========================================="
echo "VPC Network: $VPC_NETWORK_NAME"
echo "VPC Connector: $VPC_CONNECTOR_NAME"
echo "Región: $REGION"
echo ""
echo "Para usar en Cloud Run, agregar:"
echo "  --vpc-connector=$VPC_CONNECTOR_NAME"
echo "  --vpc-egress=private-ranges-only"
echo ""
if [ -n "$CLOUD_SQL_INSTANCE" ]; then
    echo "Cloud SQL Instance: $CLOUD_SQL_INSTANCE"
    echo "Usar Private IP en la conexión JDBC"
fi

