# Troubleshooting Cloud Run - Puerto 8080

## Error: Container failed to start and listen on PORT=8080

Este error indica que el contenedor no está escuchando en el puerto 8080 dentro del timeout asignado.

## Soluciones Aplicadas

### 1. Configuración del Puerto

✅ **application-prod.yml**:
```yaml
server:
  port: ${PORT:8080}
```

✅ **Dockerfile.cloud**:
- `ENV PORT=8080` (valor por defecto)
- `--server.port=${PORT:-8080}` en el comando de inicio

✅ **cloudbuild.yaml**:
- `PORT=8080` en variables de entorno

### 2. Timeout Aumentado

✅ **cloudbuild.yaml**:
```yaml
- '--timeout'
- '300s'  # 5 minutos
```

### 3. Recursos Configurados

✅ **cloudbuild.yaml**:
```yaml
- '--cpu'
- '1'
- '--memory'
- '512Mi'
```

## Posibles Causas del Error

### 1. La aplicación no inicia correctamente

**Síntomas:**
- La aplicación falla al conectarse a la base de datos
- Error al cargar secretos de Secret Manager
- Excepciones durante el inicio

**Solución:**
- Revisar logs de Cloud Run: `gcloud run services logs read parking-backend --region us-central1`
- Verificar que los secretos existan y tengan permisos correctos
- Verificar que la instancia de Cloud SQL esté activa

### 2. Timeout muy corto

**Síntomas:**
- La aplicación inicia pero tarda más de lo esperado
- Timeout antes de que la aplicación esté lista

**Solución:**
- Aumentar timeout: `--timeout 300s` (ya configurado)
- Optimizar tiempo de inicio de la aplicación

### 3. Puerto incorrecto

**Síntomas:**
- La aplicación escucha en un puerto diferente a 8080

**Solución:**
- Verificar que `server.port=${PORT:8080}` esté en application-prod.yml
- Verificar que `--server.port=${PORT:-8080}` esté en el ENTRYPOINT
- Verificar que `PORT=8080` esté en las variables de entorno

## Verificación

### 1. Ver logs de Cloud Run

```bash
gcloud run services logs read parking-backend --region us-central1 --limit 50
```

Buscar:
- `Started UsersMsApplication` - indica que la aplicación inició
- `Tomcat started on port(s): 8080` - indica que está escuchando en 8080
- Errores de conexión a base de datos
- Errores de Secret Manager

### 2. Verificar configuración del servicio

```bash
gcloud run services describe parking-backend --region us-central1
```

Verificar:
- `port: 8080`
- `timeout: 300s`
- Variables de entorno: `PORT=8080`

### 3. Probar localmente con Docker

```bash
docker build -f Dockerfile.cloud -t parking-backend-test .
docker run -p 8080:8080 -e PORT=8080 -e SPRING_PROFILES_ACTIVE=prod parking-backend-test
```

Verificar que escuche en `http://localhost:8080`

## Comandos Útiles

### Ver logs en tiempo real

```bash
gcloud run services logs tail parking-backend --region us-central1
```

### Ver detalles del servicio

```bash
gcloud run services describe parking-backend --region us-central1 --format=yaml
```

### Actualizar solo el timeout

```bash
gcloud run services update parking-backend \
  --region us-central1 \
  --timeout 300s
```

### Ver revisiones y su estado

```bash
gcloud run revisions list --service parking-backend --region us-central1
```

## Configuración Recomendada

```yaml
# cloudbuild.yaml
- '--timeout'
- '300s'  # 5 minutos para aplicaciones que cargan secretos
- '--cpu'
- '1'
- '--memory'
- '512Mi'  # Aumentar si la aplicación requiere más memoria
- '--min-instances'
- '0'  # Escalar a cero cuando no hay tráfico
- '--max-instances'
- '10'
```

## Próximos Pasos

1. **Revisar logs** para identificar el error específico
2. **Verificar secretos** de Secret Manager
3. **Verificar conexión** a Cloud SQL
4. **Aumentar timeout** si la aplicación tarda en iniciar
5. **Aumentar memoria** si hay OutOfMemoryError

