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
@Table(name = "user_printer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class UserPrinter {

    private static final String USER_PRINTER_ID_SEQ = "user_printer_user_printer_id_seq";

    @Id
    @GeneratedValue(generator = USER_PRINTER_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = USER_PRINTER_ID_SEQ, sequenceName = USER_PRINTER_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long userPrinterId;

    @ManyToOne
    @JoinColumn(name = "user_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "printer_printer_id")
    private Printer printer;

    private Boolean isActive;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

}

