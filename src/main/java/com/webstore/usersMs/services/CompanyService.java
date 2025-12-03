package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DCompany;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyService {

    DCompany create(DCompany company) throws WbException;

    DCompany update(DCompany company) throws WbException;

    List<DCompany> getBy(Long companyId, String companyName, String numberIdentity) throws WbException;

    Page<DCompany> findByPageable(Long companyId, String companyName, String numberIdentity, Pageable pageable);

    /**
     * Obtiene la empresa del usuario autenticado.
     * El companyId se obtiene automáticamente del SecurityContext.
     * 
     * @return La empresa del usuario autenticado, null si no tiene empresa asociada
     * @throws WbException Si el usuario no está autenticado o no tiene empresa
     */
    DCompany getCurrentUserCompany() throws WbException;
}

