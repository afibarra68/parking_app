# Merge Request: feature/ajustes-client-paymentday → main

## 📋 Resumen

Este merge request implementa la funcionalidad para ignorar el campo `paymentDay` durante las operaciones de creación y actualización de clientes en el backend.

## 🎯 Objetivo

El campo `paymentDay` debe ser ignorado en las operaciones de creación y actualización de clientes, pero seguir siendo visible en las consultas.

## 🔧 Cambios Realizados

### Archivos Modificados
- `src/main/java/com/webstore/usersMs/mappers/ClientMapper.java`
  - Agregado `@Mapping(target = "paymentDay", ignore = true)` en `fromDto()`
  - Agregado `@Mapping(target = "paymentDay", ignore = true)` en `merge()`

### Archivos Nuevos
- `CHANGELOG.md` - Notas de release y changelog del proyecto

## ✅ Verificación

- [x] El campo `paymentDay` se ignora en creación de clientes
- [x] El campo `paymentDay` se ignora en actualización de clientes
- [x] El campo `paymentDay` sigue siendo visible en consultas
- [x] No hay errores de compilación
- [x] CHANGELOG.md actualizado

## 📝 Notas

- El método `toDto()` no fue modificado, por lo que el campo `paymentDay` seguirá apareciendo en las respuestas de consulta.
- Este cambio es compatible con versiones anteriores ya que solo afecta las operaciones de escritura.

## 🚀 Próximos Pasos

1. Revisar y aprobar este merge request
2. Hacer merge a `main`
3. Crear tag de versión `v1.0.1` después del merge
4. Coordinar con el frontend para asegurar compatibilidad

## 👤 Autor

Cambios realizados como parte de la tarea de ajustes del módulo de clientes.

---

**Estado**: ✅ Listo para merge
**Rama origen**: `feature/ajustes-client-paymentday`
**Rama destino**: `main`

