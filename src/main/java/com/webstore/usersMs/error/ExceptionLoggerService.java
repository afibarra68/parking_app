package com.webstore.usersMs.error;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Optional;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class ExceptionLoggerService {

    void logEx(Logger toLogg, Throwable th) {
        toLogg.log(Level.ERROR, getMessage(th), Optional.of(getRootCauseMessage(th)).map(causeOnRoot -> th).orElse(th));
    }

    private String getMessage(Throwable throwable) {
        if (isNull(throwable)) {
            return "generic error data";
        }
        String msg;

        if (throwable instanceof WbException) {
            msg = throwable.getMessage();
        } else  {
            msg = getRootCauseMessage(throwable);
        }
        return msg;
    }
}