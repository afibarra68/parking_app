package com.webstore.usersMs.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DTicketTemplate {

    private Long ticketTemplateId;

    private String template; // tirilla - contenido del template ESC/POS

    private String printerType; // tipo de impresora para imprimir (COM, WINDOWS, NETWORK)

    private String ticketType; // tipo de tirilla (INGRESO, SALIDA, FACTURA, COMPROBANTE_INGRESO)

    private String invoice; // factura - template para facturas

    private String entryReceipt; // comprobante de ingreso - template para comprobantes de ingreso

    private Long companyCompanyId;

    private Long userUserId;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

}

