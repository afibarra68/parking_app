package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DPrinter;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PrinterInterface {

    DPrinter create(DPrinter printer) throws WbException;

    DPrinter update(DPrinter printer) throws WbException;

    List<DPrinter> getBy(Long printerId, String printerName, String printerType, Long companyCompanyId, Long userUserId, Boolean isActive) throws WbException;

    Page<DPrinter> findByPageable(Long printerId, String printerName, String printerType, Long companyCompanyId, Long userUserId, Boolean isActive, Pageable pageable);

}

