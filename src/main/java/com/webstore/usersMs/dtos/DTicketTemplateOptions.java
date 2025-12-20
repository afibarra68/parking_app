package com.webstore.usersMs.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.webstore.usersMs.entities.enums.ELargeVariableTicket;
import com.webstore.usersMs.entities.enums.EPrinterType;
import com.webstore.usersMs.entities.enums.ETicketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DTicketTemplateOptions {

    private Long ticketTemplateId;

    private EPrinterType printerType;

    private ETicketType ticketType;

    private ELargeVariableTicket variableTicket;

    private Long companyId;

    private Long userServiceId;

    private boolean validateAvailablePrinters;

    private boolean sendMail = false;

}

