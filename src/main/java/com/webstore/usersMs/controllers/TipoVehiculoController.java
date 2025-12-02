package com.webstore.usersMs.controllers;

import com.webstore.usersMs.error.handlers.enums.EnumResource;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RestController
@Log4j2
@RequestMapping(value = "/tipos-vehiculo")
public class TipoVehiculoController {

    @GetMapping
    public List<EnumResource> getTiposVehiculo() {
        List<EnumResource> tipos = new ArrayList<>();
        
        try {
            // Usar reflexión para obtener los valores del enum
            Class<?> enumClass = ETipoVehiculo.class;
            Method valuesMethod = enumClass.getMethod("values");
            ETipoVehiculo[] enumValues = (ETipoVehiculo[]) valuesMethod.invoke(null);
            
            // Procesar cada valor del enum usando reflexión
            for (ETipoVehiculo tipo : enumValues) {
                String name = tipo.name();
                String description = getDescription(tipo);
                tipos.add(new EnumResource(name, description, description));
            }
        } catch (Exception e) {
            log.error("Error al obtener tipos de vehículo usando reflexión", e);
            // Fallback: retornar valores por defecto
            tipos.add(new EnumResource("AUTOMOVIL", "Automóvil", "Automóvil"));
            tipos.add(new EnumResource("MOTOCICLETA", "Motocicleta", "Motocicleta"));
            tipos.add(new EnumResource("CAMIONETA", "Camioneta", "Camioneta"));
            tipos.add(new EnumResource("CAMION", "Camión", "Camión"));
            tipos.add(new EnumResource("BICICLETA", "Bicicleta", "Bicicleta"));
            tipos.add(new EnumResource("TRILER", "Tráiler", "Tráiler"));
            tipos.add(new EnumResource("TRACTOMULA", "Tractomula", "Tractomula"));
        }
        
        return tipos;
    }

    private String getDescription(ETipoVehiculo tipo) {
        switch (tipo) {
            case AUTOMOVIL:
                return "Automóvil";
            case MOTOCICLETA:
                return "Motocicleta";
            case CAMIONETA:
                return "Camioneta";
            case CAMION:
                return "Camión";
            case BICICLETA:
                return "Bicicleta";
            case TRILER:
                return "Tráiler";
            case TRACTOMULA:
                return "Tractomula";
            default:
                return tipo.name();
        }
    }
}

