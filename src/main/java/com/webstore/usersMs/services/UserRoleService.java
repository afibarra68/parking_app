package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DUserCreated;
import com.webstore.usersMs.dtos.DUserRole;
import com.webstore.usersMs.error.WbException;

public interface UserRoleService {

    void create(DUserRole client) throws WbException;

    void deleteByRoleId(Long userRoleId);
}
