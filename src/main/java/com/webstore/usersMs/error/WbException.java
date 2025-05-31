package com.webstore.usersMs.error;

import com.webstore.usersMs.error.handlers.enums.WbErrorCode;
import lombok.Getter;

@Getter
public class WbException extends Exception {

    protected final WbErrorCode exceptionCode;

    protected final transient Object[] messageParam;
    public WbException(WbErrorCode exceptionCode, Object... messageParam) {
        this.exceptionCode = exceptionCode;
        this.messageParam = messageParam;
    }

    public WbException(String cause, WbErrorCode exceptionCode, Object... messageParam) {
        super(cause);
        this.exceptionCode = exceptionCode;
        this.messageParam = messageParam;
    }

    public String getMessage() {
        return this.exceptionCode.getLocaleMessage(messageParam);
    }
}
