package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DUserRole;
import com.webstore.usersMs.error.WbException;

public interface UserRoleService {

    DUserRole create(DUserRole client) throws WbException;

    DUserRole update(DUserRole client) throws WbException;

}
