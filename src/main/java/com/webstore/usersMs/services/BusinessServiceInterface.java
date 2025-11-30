package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DBusinessService;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BusinessServiceInterface {

    DBusinessService create(DBusinessService businessService) throws WbException;

    DBusinessService update(DBusinessService businessService) throws WbException;

    List<DBusinessService> getBy(Long businessServiceId, String principalName, String code) throws WbException;

    Page<DBusinessService> findByPageable(Long businessServiceId, String principalName, String code, Pageable pageable);

}

