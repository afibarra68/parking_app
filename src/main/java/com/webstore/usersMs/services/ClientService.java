package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DClient;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    DClient create(DClient client) throws WbException;

    DClient update(DClient client) throws WbException;

    Page<DClient> findBy(String document, Pageable pageable);
}
