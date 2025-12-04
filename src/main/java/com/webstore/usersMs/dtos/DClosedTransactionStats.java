package com.webstore.usersMs.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DClosedTransactionStats {
    
    private Long totalTransactions;
    
    private BigDecimal totalAmount;
    
    private String currency;
    
    private List<DClosedTransactionSummary> transactions;
}

