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
public class DUserPrinterType {

    private Long userPrinterTypeId;

    private Long userUserId;

    private String printerType; // COM, WINDOWS, NETWORK

    private Boolean isEnabled;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

}

