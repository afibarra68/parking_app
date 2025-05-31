package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DUser;
import com.webstore.usersMs.dtos.DUserCreated;
import com.webstore.usersMs.dtos.DUserLogin;
import com.webstore.usersMs.dtos.DUserLoginResponse;
import com.webstore.usersMs.error.WbException;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    DUserCreated create(DUser dUser) throws WbException;

    DUserLoginResponse login(DUserLogin dUser, HttpServletResponse httpServletResponse)
        throws WbException;

}
