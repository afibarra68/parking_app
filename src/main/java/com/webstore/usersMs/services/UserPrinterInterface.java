package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DUserPrinter;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserPrinterInterface {

    DUserPrinter create(DUserPrinter userPrinter) throws WbException;

    DUserPrinter update(DUserPrinter userPrinter) throws WbException;

    List<DUserPrinter> getBy(Long userPrinterId, Long userUserId, Long printerPrinterId, Boolean isActive) throws WbException;

    Page<DUserPrinter> findByPageable(Long userPrinterId, Long userUserId, Long printerPrinterId, Boolean isActive, Pageable pageable);

    void delete(Long userPrinterId) throws WbException;

}

