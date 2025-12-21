package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

import com.webstore.usersMs.entities.enums.ETicketType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticket_template")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class TicketTemplate {

    private static final String TICKET_TEMPLATE_ID_SEQ = "ticket_template_ticket_template_id_seq";

    @Id
    @GeneratedValue(generator = TICKET_TEMPLATE_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = TICKET_TEMPLATE_ID_SEQ, sequenceName = TICKET_TEMPLATE_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long ticketTemplateId;

    private String template; // tirilla - contenido del template ESC/POS

    @Enumerated(EnumType.STRING)
    private ETicketType ticketType; // tipo de tirilla (INGRESO, SALIDA, FACTURA, COMPROBANTE_INGRESO)

    private String invoice; // factura - template para facturas

    private String entryReceipt; // comprobante de ingreso - template para comprobantes de ingreso

    @ManyToOne
    @JoinColumn(name = "company_company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "user_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "printer_printer_id")
    private Printer printer;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

}
