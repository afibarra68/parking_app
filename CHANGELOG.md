# Changelog - Parking App (Backend)

Todos los cambios notables en este proyecto serán documentados en este archivo.

## [Unreleased]

### Changed
- **ClientMapper**: Se agregó `@Mapping(target = "paymentDay", ignore = true)` en los métodos `fromDto()` y `merge()` para ignorar el campo `paymentDay` durante la creación y actualización de clientes.
  - El campo `paymentDay` ya no se procesa ni se guarda en la base de datos durante las operaciones de creación y actualización.
  - El campo sigue siendo visible en las respuestas de consulta (método `toDto()`).

### Technical Details
- **Archivo modificado**: `src/main/java/com/webstore/usersMs/mappers/ClientMapper.java`
- **Métodos afectados**: 
  - `fromDto(DClient dto)` - Ignora `paymentDay` al crear entidades
  - `merge(DClient dto, @MappingTarget Client client)` - Ignora `paymentDay` al actualizar entidades
- **Método no afectado**: `toDto(Client entity)` - Sigue retornando `paymentDay` en las consultas

---

## [1.0.0] - 2025-11-29

### Added
- Implementación inicial del sistema de gestión de estacionamiento
- Autenticación JWT
- Gestión de usuarios y roles
- Gestión de países
- Gestión de clientes con paginación
- Configuración de seguridad Spring Security
- Manejo de excepciones global
- Integración con H2 Database para desarrollo

### Changed
- Mejoras en la consulta de clientes por número de identidad con soporte de paginación
- Optimización de imports en múltiples archivos Java

### Fixed
- Corrección de errores de compilación relacionados con anotaciones `@NotNull` y `@NonNull`
- Corrección de warnings de type safety en `JwtRequestFilter`
- Corrección de conversión de tipos en `WbAdviceError`

---

## Notas de Release

### Versión Actual: 1.0.1 (Unreleased)
- **Rama**: `feature/ajustes-client-paymentday`
- **Estado**: Listo para merge a `main`
- **Cambios**: Ignorar campo `paymentDay` en operaciones de creación y actualización de clientes

### Próximos Pasos
1. Merge request a `main` está listo para revisión
2. Después del merge, crear tag de versión `v1.0.1`
3. Actualizar documentación si es necesario

