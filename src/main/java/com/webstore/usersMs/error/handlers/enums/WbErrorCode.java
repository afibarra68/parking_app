package com.webstore.usersMs.error.handlers.enums;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import lombok.extern.log4j.Log4j2;

@Log4j2
public enum WbErrorCode implements IEnumResource{

    ACTION_FAILED(HttpStatus.PRECONDITION_FAILED),
    NOT_FOUND(HttpStatus.PRECONDITION_FAILED),
    INCORRECT_ACCESS(HttpStatus.PRECONDITION_FAILED),
    CLIENT_NOT_FOUND(HttpStatus.PRECONDITION_FAILED),
    INVALID_HASHING(HttpStatus.PRECONDITION_FAILED);

    private final HttpStatus httpStatus;

    WbErrorCode(HttpStatus status) {
        this.httpStatus = status;
    }
    public HttpStatus getStatusHttp() {
        return httpStatus;
    }
    public int getStatusHttpValue() {
        return httpStatus.value();
    }

    @Override
    public String getEnumKey() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getResourceKey() {
        return this.name();
    }

    @Override
    public Logger getLog() {
        return log;
    }
}
