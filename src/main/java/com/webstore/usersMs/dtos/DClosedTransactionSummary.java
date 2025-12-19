package com.webstore.usersMs.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DClosedTransactionSummary {
    
    private Long closedTransactionId;
    
    private LocalDateTime operationDate;
    
    private String timeElapsed;
    
    private BigDecimal totalAmount;
    
    private String currency;
    
    private String sellerName;
}

