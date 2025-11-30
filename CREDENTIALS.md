# Credenciales de Prueba - Parking App

## Usuario de Prueba

**Username (Number Identity):** `17777777771`
**AccesKey (Password):** `ZDdGN22VWnqRks_`

## Información del Usuario

- **Nombre:** Andres Felipe Ibarra
- **ID de Usuario:** 2
- **Roles:** USER_APP, ADMIN_APP

## Endpoint de Login

```bash
POST http://localhost:9000/auth/login
Content-Type: application/json

{
    "username": "17777777771",
    "accesKey": "ZDdGN22VWnqRks_"
}
```

## Ejemplo con PowerShell

```powershell
$body = @{username='17777777771'; accesKey='ZDdGN22VWnqRks_'} | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:9000/auth/login' -Method Post -Body $body -ContentType 'application/json'
```

## Ejemplo con cURL

```bash
curl --location 'http://localhost:9000/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "username":"17777777771",
    "accesKey":"ZDdGN22VWnqRks_"
}'
```

---
*Última actualización: Credenciales verificadas y funcionando correctamente*

