package com.webstore.usersMs.entities.enums;

import com.webstore.usersMs.error.handlers.enums.IEnumResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public enum ERole implements IEnumResource {

    SUPER_USER,
    SUPER_ADMIN,
    ADMINISTRATOR_PRINCIPAL,
    ADMIN_APP,
    USER_APP,
    AUDIT_SELLER,
    PARKING_ATTENDANT;

    private ERole parent;

    private static final String RESOURCE_PREFIX = StringUtils.upperCase(ERole.class.getSimpleName()) + ".";

    public Set<String> getSplittedRole() {
        Set<String> response = new HashSet<String>();
        response.add(this.name());
        for (ERole eRole : ERole.values()) {
            if (this == eRole.parent) {
                response.addAll(eRole.getSplittedRole());
            }
        }
        return response;
    }

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
