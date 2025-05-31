package com.webstore.usersMs.error;

import static java.util.Objects.isNull;
import com.webstore.usersMs.error.handlers.enums.WbErrorCode;

public class ValidationUtils {
    private ValidationUtils() {
    }

    public static final void fieldNotNull(WbErrorCode code, Object value) throws WbException {
        if (value == null) {
            throw new WbException(code);
        }
    }

    public static final void isTrueCondition(Boolean condition, WbErrorCode code, Object value) throws WbException {
        if (isNull(condition) || !condition) {
            throw new WbException(code);
        }
    }
}