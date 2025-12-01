package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DClosedTransaction;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClosedTransactionService {

    DClosedTransaction create(DClosedTransaction dto) throws WbException;

    DClosedTransaction update(DClosedTransaction dto) throws WbException;

    Page<DClosedTransaction> findBy(String status, Long companyCompanyId, Pageable pageable);
}

