# Instalación de Google Cloud CLI en Windows

## Opción 1: Instalador Oficial (Recomendado)

### Paso 1: Descargar el instalador

1. Visita: https://cloud.google.com/sdk/docs/install-sdk#windows
2. O descarga directamente: https://dl.google.com/dl/cloudsdk/channels/rapid/GoogleCloudSDKInstaller.exe

### Paso 2: Ejecutar el instalador

1. Ejecuta `GoogleCloudSDKInstaller.exe`
2. Sigue el asistente de instalación
3. Asegúrate de marcar la opción "Run gcloud init" al finalizar

### Paso 3: Inicializar gcloud

Abre una nueva terminal PowerShell y ejecuta:

```powershell
gcloud init
```

## Opción 2: Usando Chocolatey

Si tienes Chocolatey instalado:

```powershell
# Instalar como administrador
choco install gcloudsdk -y
```

## Opción 3: Instalación Manual con PowerShell

Ejecuta en PowerShell (como administrador):

```powershell
# Descargar el instalador
Invoke-WebRequest -Uri "https://dl.google.com/dl/cloudsdk/channels/rapid/GoogleCloudSDKInstaller.exe" -OutFile "$env:TEMP\GoogleCloudSDKInstaller.exe"

# Ejecutar el instalador
Start-Process -FilePath "$env:TEMP\GoogleCloudSDKInstaller.exe" -Wait
```

## Verificación de la Instalación

Después de instalar, cierra y vuelve a abrir PowerShell, luego ejecuta:

```powershell
gcloud version
```

Deberías ver algo como:
```
Google Cloud SDK 450.0.0
```

## Configuración Inicial

1. **Inicializar gcloud:**
```powershell
gcloud init
```

2. **Autenticarse:**
```powershell
gcloud auth login
```

3. **Configurar el proyecto:**
```powershell
gcloud config set project TU_PROJECT_ID
```

4. **Verificar configuración:**
```powershell
gcloud config list
```

## Solución de Problemas

### Si gcloud no se reconoce después de instalar:

1. Cierra y vuelve a abrir PowerShell
2. Verifica que la ruta esté en el PATH:
```powershell
$env:PATH -split ';' | Select-String "google"
```

3. Si no aparece, agrega manualmente:
```powershell
# La ruta típica es:
# C:\Program Files (x86)\Google\Cloud SDK\google-cloud-sdk\bin
# O
# C:\Users\$env:USERNAME\AppData\Local\Google\Cloud SDK\google-cloud-sdk\bin
```

### Actualizar gcloud:

```powershell
gcloud components update
```

## Próximos Pasos

Una vez instalado, sigue las instrucciones en `GCP_SETUP.md` para configurar tu proyecto.

