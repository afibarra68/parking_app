package com.webstore.usersMs.controllers;

import com.webstore.usersMs.error.handlers.enums.EnumResource;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import com.webstore.usersMs.services.EnumResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador para obtener tipos de vehículo.
 * Usa el servicio genérico EnumResourceService que funciona con cualquier enum
 * que implemente IEnumResource.
 */
@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping(value = "/enum")
public class TipoVehiculoController {

    private final EnumResourceService enumResourceService;

    @GetMapping
    public List<EnumResource> getTiposVehiculo() {
        try {
            // Usar el servicio genérico que carga desde enum.properties usando reflexión
            return enumResourceService.getEnumResources(ETipoVehiculo.class);
        } catch (Exception e) {
            log.error("Error al obtener tipos de vehículo", e);
            throw e; // Dejar que Spring maneje el errorC
        }
    }
}
