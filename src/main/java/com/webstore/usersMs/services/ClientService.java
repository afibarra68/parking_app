package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DClient;
import com.webstore.usersMs.error.WbException;

public interface ClientService {

    DClient create(DClient client) throws WbException;

    DClient update(DClient client) throws WbException;

}
