package com.webstore.usersMs.error.handlers.enums;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import lombok.extern.log4j.Log4j2;

@Log4j2
public enum WbErrorCode implements IEnumResource {

    ACTION_FAILED(HttpStatus.PRECONDITION_FAILED),
    NOT_FOUND(HttpStatus.PRECONDITION_FAILED),
    INCORRECT_ACCESS(HttpStatus.PRECONDITION_FAILED),
    CLIENT_NOT_FOUND(HttpStatus.PRECONDITION_FAILED),
    COMPANY_SELLER_NOT_FOUND(HttpStatus.PRECONDITION_FAILED),
    CAN_NOT_CREATE_USER(HttpStatus.PRECONDITION_FAILED),
    ACCESS_DENIED(HttpStatus.FORBIDDEN),
    ACCESS_DENIED_NO_COMPANY(HttpStatus.FORBIDDEN),
    OPEN_TRANSACTION_NOT_FOUND(HttpStatus.PRECONDITION_FAILED),
    TRANSACTION_STATUS_NOT_OPEN(HttpStatus.PRECONDITION_FAILED),
    BILLING_PRICE_NOT_FOUND(HttpStatus.PRECONDITION_FAILED),
    INVALID_HASHING(HttpStatus.PRECONDITION_FAILED),
    BILLING_PRICE_RANGE_OVERLAP(HttpStatus.PRECONDITION_FAILED),
    BILLING_PRICE_TIME_EXCEEDED(HttpStatus.PRECONDITION_FAILED),
    COUNTRY_HAS_RELATIONS(HttpStatus.PRECONDITION_FAILED),
    NO_TEMPLATE_FLAT_FOR_BUILDING(HttpStatus.PRECONDITION_FAILED),
    USER_NOT_FOUND(HttpStatus.PRECONDITION_FAILED);

    private final HttpStatus httpStatus;

    private static final String PROPERTIES_NAME = "com/webstore/usersMs/exceptions/exception";

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
    public String getPropertiesName() {
        return PROPERTIES_NAME;
    }


    @Override
    public Logger getLog() {
        return log;
    }
}
