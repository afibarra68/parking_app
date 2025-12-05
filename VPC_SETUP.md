# Configuración de VPC para Parking App

Esta guía explica cómo configurar VPC (Virtual Private Cloud) para que el servicio se conecte a Cloud SQL usando direcciones IP privadas asignadas por VPC.

## Beneficios de usar VPC con IP Privada

- **Seguridad mejorada**: El tráfico no sale a Internet
- **Mejor rendimiento**: Conexión directa a través de la red privada
- **Cumplimiento**: Cumple con requisitos de seguridad que requieren tráfico privado
- **Costos**: Puede reducir costos al evitar tráfico público

## Arquitectura

```
Cloud Run (parking-backend)
    ↓ (VPC Connector)
VPC Network (parking-vpc)
    ↓ (Private IP)
Cloud SQL (Private IP)
```

## Prerrequisitos

1. Proyecto de Google Cloud configurado
2. `gcloud` CLI instalado y configurado
3. Permisos de administrador en el proyecto

## Paso 1: Configurar VPC y VPC Connector

Ejecuta el script de configuración:

```bash
# Configurar variables
export GCP_PROJECT_ID=649345514504
export GCP_REGION=us-central1
export CLOUD_SQL_INSTANCE=tu-instancia-sql

# Dar permisos de ejecución
chmod +x scripts/setup-vpc.sh

# Ejecutar script
./scripts/setup-vpc.sh
```

O manualmente:

```bash
# 1. Habilitar APIs
gcloud services enable \
    compute.googleapis.com \
    vpcaccess.googleapis.com \
    servicenetworking.googleapis.com

# 2. Crear VPC Network
gcloud compute networks create parking-vpc \
    --subnet-mode=auto \
    --bgp-routing-mode=regional

# 3. Crear VPC Connector para Cloud Run
gcloud compute networks vpc-access connectors create parking-vpc-connector \
    --region=us-central1 \
    --network=parking-vpc \
    --range=10.8.0.0/28 \
    --min-instances=2 \
    --max-instances=3 \
    --machine-type=e2-micro
```

## Paso 2: Configurar Cloud SQL con Private IP

```bash
# 1. Reservar rango de IP para Cloud SQL
gcloud compute addresses create google-managed-services-parking-vpc \
    --global \
    --purpose=VPC_PEERING \
    --prefix-length=16 \
    --network=projects/649345514504/global/networks/parking-vpc

# 2. Conectar servicio de red privada
gcloud services vpc-peerings connect \
    --service=servicenetworking.googleapis.com \
    --ranges=google-managed-services-parking-vpc \
    --network=parking-vpc

# 3. Asignar IP privada a Cloud SQL
gcloud sql instances patch TU_INSTANCIA_SQL \
    --network=projects/649345514504/global/networks/parking-vpc \
    --no-assign-ip
```

## Paso 3: Obtener la IP Privada de Cloud SQL

```bash
# Ver información de IPs
gcloud sql instances describe TU_INSTANCIA_SQL \
    --format="table(ipAddresses[].type,ipAddresses[].ipAddress)"

# Obtener solo la IP privada
gcloud sql instances describe TU_INSTANCIA_SQL \
    --format="value(ipAddresses[?type=='PRIVATE'].ipAddress)"
```

## Paso 4: Actualizar el Secreto postgres_auth

Actualiza el secreto en Secret Manager con la IP privada:

```json
{
  "username": "postgres",
  "password": "tu-password",
  "private_ip": "10.x.x.x",
  "database": "db_name",
  "port": "5432"
}
```

O con la URL completa:

```json
{
  "username": "postgres",
  "password": "tu-password",
  "url": "jdbc:postgresql://10.x.x.x:5432/db_name",
  "database": "db_name"
}
```

Para actualizar el secreto:

```bash
echo -n '{"username":"postgres","password":"password","private_ip":"10.x.x.x","database":"db_name"}' | \
  gcloud secrets versions add postgres_auth \
    --data-file=- \
    --project=649345514504
```

## Paso 5: Desplegar Cloud Run con VPC Connector

### Opción A: Usando Cloud Build

Actualiza `cloudbuild.yaml` descomentando las líneas de VPC:

```yaml
- '--vpc-connector'
- '${_VPC_CONNECTOR_NAME}'
- '--vpc-egress'
- 'private-ranges-only'
```

Y configura la variable de sustitución:

```bash
gcloud builds submit --config=cloudbuild.yaml \
  --substitutions=_CLOUD_SQL_INSTANCE=649345514504:us-central1:instancia,_VPC_CONNECTOR_NAME=parking-vpc-connector
```

### Opción B: Despliegue Manual

```bash
gcloud run deploy parking-backend \
  --image gcr.io/649345514504/parking-backend:latest \
  --region us-central1 \
  --platform managed \
  --vpc-connector parking-vpc-connector \
  --vpc-egress private-ranges-only \
  --add-cloudsql-instances 649345514504:us-central1:instancia \
  --set-env-vars SPRING_PROFILES_ACTIVE=prod \
  --set-secrets SPRING_DATASOURCE_URL=parking-app-db-url:latest,SPRING_DATASOURCE_USERNAME=parking-app-db-username:latest,SPRING_DATASOURCE_PASSWORD=parking-app-db-password:latest
```

## Opciones de VPC Egress

- `all-traffic`: Todo el tráfico sale por VPC
- `private-ranges-only`: Solo tráfico a rangos privados (recomendado para Cloud SQL)
- `all`: Todo el tráfico puede usar VPC o Internet

## Verificación

### Verificar VPC Connector

```bash
gcloud compute networks vpc-access connectors describe parking-vpc-connector \
    --region=us-central1
```

### Verificar IP Privada de Cloud SQL

```bash
./scripts/get-vpc-info.sh
```

### Probar Conexión

La aplicación debería conectarse automáticamente usando la IP privada cuando:
1. El secreto contiene `private_ip` o la URL con IP privada
2. Cloud Run está configurado con VPC Connector
3. Cloud SQL tiene IP privada asignada

## Troubleshooting

### Error: VPC Connector no encontrado
- Verificar que el VPC Connector existe: `gcloud compute networks vpc-access connectors list`
- Verificar que está en la misma región que Cloud Run

### Error: No se puede conectar a Cloud SQL
- Verificar que Cloud SQL tiene IP privada asignada
- Verificar que el VPC Connector puede alcanzar la red de Cloud SQL
- Verificar que el rango de IP del VPC Connector no se solapa con Cloud SQL

### Error: Timeout de conexión
- Verificar que `--vpc-egress` está configurado correctamente
- Verificar que la IP privada en el secreto es correcta
- Verificar logs de Cloud Run para ver errores de conexión

### Verificar conectividad

```bash
# Desde Cloud Run (usando Cloud Shell o Cloud Build)
# La aplicación debería poder resolver la IP privada
ping 10.x.x.x  # IP privada de Cloud SQL
```

## Costos

- **VPC Connector**: Se cobra por instancia activa (mínimo 2 instancias)
- **Cloud SQL Private IP**: Sin costo adicional
- **Tráfico VPC**: Sin costo adicional (vs tráfico público)

## Seguridad

- El tráfico entre Cloud Run y Cloud SQL nunca sale a Internet
- Las IPs privadas no son accesibles desde fuera de la VPC
- Se recomienda usar `private-ranges-only` para máximo control

## Referencias

- [VPC Connector para Cloud Run](https://cloud.google.com/run/docs/configuring/vpc)
- [Cloud SQL Private IP](https://cloud.google.com/sql/docs/postgres/configure-private-ip)
- [VPC Peering](https://cloud.google.com/vpc/docs/vpc-peering)

