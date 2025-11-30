# Instrucciones de Release - Parking App (Backend)

## 📦 Estado Actual

- **Rama actual**: `feature/ajustes-client-paymentday`
- **Commit**: `7017118` - "feat: ignorar campo paymentDay en creación y actualización de clientes"
- **Estado**: ✅ Listo para push y merge request

## 🚀 Pasos para Publicar

### 1. Push de la rama a GitHub/GitLab

```bash
cd parking-app
git push -u origin feature/ajustes-client-paymentday
```

### 2. Crear Merge Request en GitHub/GitLab

1. Ir al repositorio en GitHub/GitLab
2. Click en "Pull Requests" o "Merge Requests"
3. Click en "New Pull Request" o "New Merge Request"
4. Seleccionar:
   - **Base branch**: `main`
   - **Compare branch**: `feature/ajustes-client-paymentday`
5. Título: `feat: ignorar campo paymentDay en creación y actualización de clientes`
6. Descripción: Copiar contenido de `MERGE_REQUEST.md`
7. Asignar revisores si es necesario
8. Marcar como "Ready for Review" o "Ready to merge"

### 3. Después del Merge

```bash
# Cambiar a main
git checkout main

# Actualizar main
git pull origin main

# Crear tag de versión
git tag -a v1.0.1 -m "Release v1.0.1: Ignorar paymentDay en creación/actualización de clientes"
git push origin v1.0.1

# Opcional: Eliminar rama feature local
git branch -d feature/ajustes-client-paymentday
```

## 📝 Resumen de Cambios

### Archivos Modificados
- `src/main/java/com/webstore/usersMs/mappers/ClientMapper.java`

### Archivos Nuevos
- `CHANGELOG.md`
- `MERGE_REQUEST.md`
- `RELEASE_INSTRUCTIONS.md`

### Cambios Técnicos
- Agregado `@Mapping(target = "paymentDay", ignore = true)` en `fromDto()`
- Agregado `@Mapping(target = "paymentDay", ignore = true)` en `merge()`
- El campo `paymentDay` se ignora en creación y actualización
- El campo sigue visible en consultas (`toDto()`)

## ✅ Checklist Pre-Merge

- [x] Código compilado sin errores
- [x] CHANGELOG.md actualizado
- [x] MERGE_REQUEST.md creado
- [x] Commit con mensaje descriptivo
- [ ] Push realizado
- [ ] Merge Request creado
- [ ] Revisión aprobada
- [ ] Merge completado
- [ ] Tag de versión creado

---

**Autor**: Sistema de gestión de releases
**Fecha**: 2025-11-29

