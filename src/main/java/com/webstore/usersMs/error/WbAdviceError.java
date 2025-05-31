package com.webstore.usersMs.error;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.webstore.usersMs.error.handlers.ExceptionsHandler;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
@PropertySource("classpath:exceptions/exception.properties")
public class WbAdviceError {

    private final ExceptionLoggerService service;

    @ExceptionHandler({WbException.class})
    public ResponseEntity<ExceptionsHandler> handleException(WbException ex) throws WbException {
        service.logEx(log, ex);
        return ResponseEntity
                .status(ex.exceptionCode.getStatusHttp())
                .body(ExceptionsHandler.builder()
                        .code(ex.getExceptionCode().getStatusHttpValue())
                        .wbErrorCode(ex.getExceptionCode())
                        .messageLocated(ex.getLocalizedMessage())
                        .readableMsg(ex.getMessage())
                        .build());
    }
}
