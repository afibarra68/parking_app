package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DUserPrinterType;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserPrinterTypeInterface {

    DUserPrinterType create(DUserPrinterType userPrinterType) throws WbException;

    DUserPrinterType update(DUserPrinterType userPrinterType) throws WbException;

    List<DUserPrinterType> getBy(Long userPrinterTypeId, Long userUserId, String printerType, Boolean isEnabled) throws WbException;

    Page<DUserPrinterType> findByPageable(Long userPrinterTypeId, Long userUserId, String printerType, Boolean isEnabled, Pageable pageable);

    void delete(Long userPrinterTypeId) throws WbException;

}

