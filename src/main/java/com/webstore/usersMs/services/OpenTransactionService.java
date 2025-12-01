package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DOpenTransaction;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OpenTransactionService {

    DOpenTransaction create(DOpenTransaction transaction) throws WbException;

    DOpenTransaction update(DOpenTransaction transaction) throws WbException;

    Page<DOpenTransaction> findBy(String status, Long companyCompanyId, Pageable pageable);
}

