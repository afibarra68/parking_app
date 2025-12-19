package com.webstore.usersMs.controllers;

import com.webstore.usersMs.error.handlers.enums.EnumResource;
import com.webstore.usersMs.error.handlers.enums.IEnumResource;
import com.webstore.usersMs.services.EnumResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador genérico para obtener recursos de enums que implementan
 * IEnumResource.
 * Permite obtener la lista transformada de cualquier enum pasando su nombre
 * como parámetro.
 * 
 * Ejemplos de uso:
 * - GET /enum-resources/ETipoVehiculo
 * - GET
 * /enum-resources?enumClass=com.webstore.usersMs.entities.enums.ETipoVehiculo
 * - GET /enum-resources/ERole
 */
@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping(value = "/enums")
public class EnumResourceController {

    private static final String DEFAULT_ENUM_PACKAGE = "com.webstore.usersMs.entities.enums.";

    private final EnumResourceService enumResourceService;

    /**
     * Obtiene los recursos de un enum usando su nombre simple (ej: ETipoVehiculo).
     * Busca el enum en el paquete por defecto: com.webstore.usersMs.entities.enums
     * 
     * @param enumName Nombre simple del enum (ej: "ETipoVehiculo", "ERole",
     *                 "EtransactionStatus")
     * @return Lista de EnumResource con id, description y descriptionExtended
     */
    @GetMapping("/{enumName}")
    public ResponseEntity<List<EnumResource>> getEnumResourcesByName(
            @PathVariable String enumName) {
        try {
            // Construir el nombre completo de la clase
            String fullClassName = DEFAULT_ENUM_PACKAGE + enumName;
            Class<?> enumClass = Class.forName(fullClassName);

            // Validar y obtener los recursos
            List<EnumResource> resources = getEnumResourcesFromClass(enumClass);
            return ResponseEntity.ok(resources);

        } catch (ClassNotFoundException e) {
            log.error("No se encontró la clase del enum: {}", enumName, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        } catch (Exception e) {
            log.error("Error al obtener recursos del enum: {}", enumName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * Obtiene los recursos de un enum usando el nombre completo de la clase.
     * Permite mayor flexibilidad al especificar el paquete completo.
     * 
     * @param enumClass Nombre completo de la clase del enum (ej:
     *                  "com.webstore.usersMs.entities.enums.ETipoVehiculo")
     * @return Lista de EnumResource con id, description y descriptionExtended
     */
    @GetMapping
    public ResponseEntity<List<EnumResource>> getEnumResourcesByClass(
            @RequestParam(required = false) String enumClass) {

        // Si no se proporciona el parámetro, retornar error
        if (enumClass == null || enumClass.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Class<?> clazz = Class.forName(enumClass.trim());
            List<EnumResource> resources = getEnumResourcesFromClass(clazz);
            return ResponseEntity.ok(resources);

        } catch (ClassNotFoundException e) {
            log.error("No se encontró la clase del enum: {}", enumClass, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Argumento inválido para el enum: {}", enumClass, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al obtener recursos del enum: {}", enumClass, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * Método auxiliar que valida y obtiene los recursos de un enum.
     * 
     * @param enumClass La clase del enum
     * @return Lista de EnumResource
     * @throws IllegalArgumentException Si la clase no es un enum o no implementa
     *                                  IEnumResource
     */
    @SuppressWarnings("unchecked")
    private <T extends Enum<T> & IEnumResource> List<EnumResource> getEnumResourcesFromClass(Class<?> enumClass) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException(
                    "La clase proporcionada no es un enum: " + enumClass.getName());
        }

        if (!IEnumResource.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException(
                    "El enum debe implementar IEnumResource: " + enumClass.getName());
        }

        // Usar el servicio genérico para obtener los recursos
        // El cast es seguro porque ya validamos que es un enum e implementa
        // IEnumResource
        return enumResourceService.getEnumResources((Class<T>) enumClass);
    }
}
