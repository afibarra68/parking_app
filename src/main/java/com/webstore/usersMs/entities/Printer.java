package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Entity;
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

    private String printerType; // COM, WINDOWS, NETWORK

    private String connectionString; // COM port, printer name, or network address

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

