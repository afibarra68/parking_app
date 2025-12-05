# Configuración de Secretos en Google Cloud

Esta guía explica cómo configurar el secreto `passwirk_sec:latest` para la variable de entorno `SPRING_DATASOURCE_PASSWORD` en Google Cloud Run.

## Tabla de Contenidos

- [Prerrequisitos](#prerrequisitos)
- [Crear o Actualizar el Secreto](#crear-o-actualizar-el-secreto)
- [Configurar el Secreto en Cloud Run](#configurar-el-secreto-en-cloud-run)
- [Verificar la Configuración](#verificar-la-configuración)
- [Troubleshooting](#troubleshooting)

## Prerrequisitos

1. **Google Cloud CLI instalado y configurado**
   ```bash
   gcloud --version
   gcloud auth login
   gcloud config set project testauth20-394320
   ```

2. **API de Secret Manager habilitada**
   ```bash
   gcloud services enable secretmanager.googleapis.com
   ```

3. **Permisos necesarios**
   - `roles/secretmanager.admin` o `roles/secretmanager.secretAccessor`

## Crear o Actualizar el Secreto

### Verificar si el Secreto Existe

```bash
gcloud secrets describe passwirk_sec --project=testauth20-394320
```

### Crear el Secreto (si no existe)

#### Opción A: Contraseña como String Simple (Recomendado)

```bash
echo -n "tu-contraseña-segura" | \
  gcloud secrets create passwirk_sec \
    --data-file=- \
    --replication-policy="automatic" \
    --project=testauth20-394320
```

#### Opción B: Contraseña como JSON

```bash
echo -n '{"password":"tu-contraseña-segura"}' | \
  gcloud secrets create passwirk_sec \
    --data-file=- \
    --replication-policy="automatic" \
    --project=testauth20-394320
```

### Actualizar el Secreto (si ya existe)

#### Opción A: Contraseña como String Simple

```bash
echo -n "tu-nueva-contraseña" | \
  gcloud secrets versions add passwirk_sec \
    --data-file=- \
    --project=testauth20-394320
```

#### Opción B: Contraseña como JSON

```bash
echo -n '{"password":"tu-nueva-contraseña"}' | \
  gcloud secrets versions add passwirk_sec \
    --data-file=- \
    --project=testauth20-394320
```

### Verificar el Secreto

```bash
# Listar versiones del secreto
gcloud secrets versions list passwirk_sec --project=testauth20-394320

# Ver el contenido (sin mostrarlo en pantalla)
gcloud secrets versions access latest --secret=passwirk_sec --project=testauth20-394320 | wc -c
```

## Configurar el Secreto en Cloud Run

### Sintaxis

La sintaxis para configurar un secreto en Cloud Run es:

```bash
--set-secrets "VARIABLE_ENV=SECRET_NAME:VERSION"
```

Donde:
- `VARIABLE_ENV`: Nombre de la variable de entorno (`SPRING_DATASOURCE_PASSWORD`)
- `SECRET_NAME`: Nombre del secreto en Google Cloud Secret Manager (`passwirk_sec`)
- `VERSION`: Versión del secreto (`latest` para la versión más reciente)

### Comando Completo

```bash
gcloud run deploy parking-backend \
  --image gcr.io/testauth20-394320/parking-backend:latest \
  --platform managed \
  --region us-central1 \
  --project testauth20-394320 \
  --set-secrets "SPRING_DATASOURCE_PASSWORD=passwirk_sec:latest"
```

### Actualizar Solo el Secreto en un Servicio Existente

```bash
gcloud run services update parking-backend \
  --region us-central1 \
  --project testauth20-394320 \
  --set-secrets "SPRING_DATASOURCE_PASSWORD=passwirk_sec:latest"
```

### Múltiples Secretos (si necesitas otros)

Si necesitas configurar múltiples secretos, sepáralos con comas:

```bash
gcloud run deploy parking-backend \
  --image gcr.io/testauth20-394320/parking-backend:latest \
  --platform managed \
  --region us-central1 \
  --project testauth20-394320 \
  --set-secrets "SPRING_DATASOURCE_PASSWORD=passwirk_sec:latest,SPRING_JWT_SECRET=jwt-secret:latest"
```

## Permisos del Service Account

El service account de Cloud Run necesita acceso al secreto para poder leerlo.

### Obtener el Service Account del Servicio

```bash
gcloud run services describe parking-backend \
  --region us-central1 \
  --project testauth20-394320 \
  --format 'value(spec.template.spec.serviceAccountName)'
```

### Otorgar Permisos al Service Account

```bash
# Usar el service account por defecto de Compute Engine
gcloud secrets add-iam-policy-binding passwirk_sec \
  --member="serviceAccount:testauth20-394320-compute@developer.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor" \
  --project=testauth20-394320
```

O si usas un service account personalizado:

```bash
# Reemplazar con tu service account
export SERVICE_ACCOUNT="tu-service-account@testauth20-394320.iam.gserviceaccount.com"

gcloud secrets add-iam-policy-binding passwirk_sec \
  --member="serviceAccount:${SERVICE_ACCOUNT}" \
  --role="roles/secretmanager.secretAccessor" \
  --project=testauth20-394320
```

### Verificar Permisos

```bash
gcloud secrets get-iam-policy passwirk_sec --project=testauth20-394320
```

## Verificar la Configuración

### 1. Verificar que el Secreto Está Configurado en Cloud Run

```bash
gcloud run services describe parking-backend \
  --region us-central1 \
  --project testauth20-394320 \
  --format "value(spec.template.spec.containers[0].env)" | grep SPRING_DATASOURCE_PASSWORD
```

### 2. Ver Logs del Servicio

```bash
gcloud run services logs read parking-backend \
  --region us-central1 \
  --project testauth20-394320 \
  --limit 50
```

Buscar en los logs:
- `"Contraseña cargada desde secreto 'passwirk_sec' y configurada"`
- `"Propiedades de Secret Manager configuradas exitosamente"`
- `"Contraseña de base de datos configurada desde Secret Manager"`

### 3. Probar la Conexión

```bash
# Obtener la URL del servicio
export SERVICE_URL=$(gcloud run services describe parking-backend \
  --region us-central1 \
  --format 'value(status.url)')

# Probar health check
curl ${SERVICE_URL}/actuator/health
```

## Troubleshooting

### Error: Permission denied al acceder al secreto

**Solución:**
```bash
# Verificar permisos del service account
gcloud secrets get-iam-policy passwirk_sec --project=testauth20-394320

# Otorgar permisos si faltan
gcloud secrets add-iam-policy-binding passwirk_sec \
  --member="serviceAccount:testauth20-394320-compute@developer.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor" \
  --project=testauth20-394320
```

### Error: Secret not found

**Solución:**
```bash
# Verificar que el secreto existe
gcloud secrets describe passwirk_sec --project=testauth20-394320

# Si no existe, crearlo (ver sección "Crear o Actualizar el Secreto")
```

### Error: La contraseña no funciona

**Posibles causas:**
1. El secreto `passwirk_sec` no está configurado correctamente
2. La variable de entorno `SPRING_DATASOURCE_PASSWORD` no se está inyectando
3. El formato del secreto no es correcto

**Solución:**
```bash
# Verificar el valor del secreto (sin mostrarlo)
gcloud secrets versions access latest --secret=passwirk_sec --project=testauth20-394320 | wc -c

# Verificar que la variable está configurada en Cloud Run
gcloud run services describe parking-backend \
  --region us-central1 \
  --format "value(spec.template.spec.containers[0].env)" | grep SPRING_DATASOURCE_PASSWORD

# Revisar logs para ver si SecretManagerConfig está cargando el secreto
gcloud run services logs read parking-backend \
  --region us-central1 \
  --limit 100 | grep -i "password\|secret"
```

### Error: Version not found

**Solución:**
```bash
# Verificar que el secreto tiene versiones
gcloud secrets versions list passwirk_sec --project=testauth20-394320

# Si no hay versiones, crear una nueva
echo -n "tu-contraseña" | \
  gcloud secrets versions add passwirk_sec \
    --data-file=- \
    --project=testauth20-394320
```

## Comandos Rápidos

```bash
# Crear/Actualizar secreto
echo -n "tu-contraseña" | gcloud secrets versions add passwirk_sec --data-file=- --project=testauth20-394320

# Configurar secreto en Cloud Run
gcloud run services update parking-backend \
  --region us-central1 \
  --project testauth20-394320 \
  --set-secrets "SPRING_DATASOURCE_PASSWORD=passwirk_sec:latest"

# Verificar configuración
gcloud run services describe parking-backend \
  --region us-central1 \
  --format "value(spec.template.spec.containers[0].env)" | grep SPRING_DATASOURCE_PASSWORD

# Ver logs
gcloud run services logs read parking-backend --region us-central1 --limit 50

# Otorgar permisos
gcloud secrets add-iam-policy-binding passwirk_sec \
  --member="serviceAccount:testauth20-394320-compute@developer.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor" \
  --project=testauth20-394320
```

## Referencias

- [SECRET_MANAGER_SETUP.md](./SECRET_MANAGER_SETUP.md) - Detalles sobre cómo funciona Secret Manager en la aplicación
- [GCP_SETUP.md](./GCP_SETUP.md) - Configuración general de Google Cloud
- [Documentación oficial de Google Cloud Secret Manager](https://cloud.google.com/secret-manager/docs)
- [Documentación de Cloud Run con Secret Manager](https://cloud.google.com/run/docs/configuring/secrets)
