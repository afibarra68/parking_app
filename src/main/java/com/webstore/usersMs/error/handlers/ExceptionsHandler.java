package com.webstore.usersMs.error.handlers;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.ACTION_FAILED;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.webstore.usersMs.error.handlers.enums.WbErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionsHandler {

    private String messageLocated;

    private String readableMsg;

    private int code;

    @Builder.Default
    private WbErrorCode wbErrorCode = ACTION_FAILED;

}