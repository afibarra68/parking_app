package com.webstore.usersMs.entities.enums;

import com.webstore.usersMs.error.handlers.enums.IEnumResource;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

@Log4j2
public enum ETipoVehiculo implements IEnumResource {

    AUTOMOVIL,
    MOTOCICLETA,
    CAMIONETA,
    CAMION,
    BICICLETA,
    TRILER,
    TRACTOMULA;

    private static final String RESOURCE_PREFIX = StringUtils.upperCase(ETipoVehiculo.class.getSimpleName()) + ".";

    @Override
    public String getEnumKey() {
        return this.name();
    }

    @Override
    public String getResourceKey() {
        return RESOURCE_PREFIX + this.name();
    }

    @Override
    public Logger getLog() {
        return log;
    }
}

