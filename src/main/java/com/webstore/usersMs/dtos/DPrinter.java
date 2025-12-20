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
public class DPrinter {

    private Long printerId;

    private String printerName;

    private String printerType;

    private String connectionString;

    private Boolean isActive;

    private Long companyCompanyId;

    private Long userUserId;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

}

