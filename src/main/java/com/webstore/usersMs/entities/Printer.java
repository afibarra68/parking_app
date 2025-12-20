package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

import com.webstore.usersMs.entities.enums.ELargeVariableTicket;
import com.webstore.usersMs.entities.enums.EPrinterType;
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
@Table(name = "printer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Printer {

    private static final String PRINTER_ID_SEQ = "printer_printer_id_seq";

    @Id
    @GeneratedValue(generator = PRINTER_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = PRINTER_ID_SEQ, sequenceName = PRINTER_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long printerId;

    private String printerName;

    @Enumerated(EnumType.STRING)
    private EPrinterType printerType;

    @Enumerated(EnumType.STRING)
    private ELargeVariableTicket paperType;

    private String connectionString;

    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "company_company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "user_user_id")
    private User user;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

}

