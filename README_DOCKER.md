# Guía de Docker y Despliegue

## Configuración Local con Docker Compose

### Desarrollo con Base de Datos Local

1. Copiar el archivo de ejemplo de variables de entorno:
```bash
cp env.example .env
```

2. Editar `.env` con tus valores locales (opcional, tiene valores por defecto)

3. Ejecutar con base de datos local:
```bash
docker-compose --profile local-db up --build
```

### Desarrollo con Cloud SQL (Producción)

1. Configurar Google Cloud (ver `GCP_SETUP.md`)

2. Cargar secretos desde Secret Manager:
```bash
export GCP_PROJECT_ID=tu-proyecto-id
source scripts/load-secrets.sh
```

3. Crear archivo `.env` con las variables cargadas o configurarlas manualmente

4. Ejecutar solo el backend (sin postgres local):
```bash
docker-compose up --build
```

## Compilación de Imagen Docker

### Para desarrollo local:
```bash
docker build -t parking-backend:local .
```

### Para Google Cloud:
```bash
docker build -f Dockerfile.cloud -t gcr.io/PROJECT_ID/parking-backend:latest .
```

## Variables de Entorno Importantes

- `SPRING_PROFILES_ACTIVE`: Perfil de Spring Boot (prod, dev)
- `SPRING_DATASOURCE_URL`: URL de conexión a la base de datos
- `SPRING_DATASOURCE_USERNAME`: Usuario de la base de datos
- `SPRING_DATASOURCE_PASSWORD`: Contraseña de la base de datos
- `SPRING_JWT_SECRET`: Secret key para JWT
- `GCP_PROJECT_ID`: ID del proyecto de Google Cloud
- `GOOGLE_APPLICATION_CREDENTIALS`: Ruta al archivo de credenciales de servicio

## Seguridad

⚠️ **NUNCA** commits archivos `.env` con credenciales reales al repositorio.

Para producción, siempre usar:
- Google Cloud Secret Manager
- Variables de entorno de Cloud Run
- Service Accounts con permisos mínimos necesarios

## Comandos Útiles

```bash
# Ver logs
docker-compose logs -f backend

# Reconstruir solo el backend
docker-compose build backend

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

