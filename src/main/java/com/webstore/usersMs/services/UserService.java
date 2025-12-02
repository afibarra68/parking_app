package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DUser;
import com.webstore.usersMs.dtos.DUserCreated;
import com.webstore.usersMs.dtos.DUserList;
import com.webstore.usersMs.dtos.DUserLogin;
import com.webstore.usersMs.dtos.DUserLoginResponse;
import com.webstore.usersMs.error.WbException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    DUserCreated create(DUser dUser) throws WbException;

    DUserLoginResponse login(DUserLogin dUser, HttpServletResponse httpServletResponse) throws WbException;

    DUserCreated getUser(String numberIdentity) throws WbException;

    DUserCreated deleteByDocument(Long userDocument);

    Page<DUserList> findByPageable(Long appUserId, String numberIdentity, Long companyCompanyId, Pageable pageable);
}
