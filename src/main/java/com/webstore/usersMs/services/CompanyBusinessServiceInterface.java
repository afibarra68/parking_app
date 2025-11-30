package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DCompanyBusinessService;
import com.webstore.usersMs.error.WbException;

import java.util.List;

public interface CompanyBusinessServiceInterface {

    DCompanyBusinessService create(DCompanyBusinessService companyBusinessService) throws WbException;

    DCompanyBusinessService update(DCompanyBusinessService companyBusinessService) throws WbException;

    List<DCompanyBusinessService> getBy(Long companyBusinessServiceId, Long companyId, Long businessServiceId) throws WbException;

    List<DCompanyBusinessService> getByCompanyId(Long companyId);

    void delete(Long companyBusinessServiceId) throws WbException;

}

