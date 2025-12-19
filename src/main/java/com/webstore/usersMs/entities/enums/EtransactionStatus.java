package com.webstore.usersMs.entities.enums;

import com.webstore.usersMs.error.handlers.enums.IEnumResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

public enum EtransactionStatus implements IEnumResource {

    OPEN,
    CLOSED;

    private static final String RESOURCE_PREFIX = StringUtils.upperCase(EtransactionStatus.class.getSimpleName()) + ".";

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
        return null;
    }
}

