package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DUserRole;
import com.webstore.usersMs.dtos.DUserRoleList;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRoleService {

    void create(DUserRole client) throws WbException;

    void update(Long userRoleId, DUserRole role) throws WbException;

    void deleteByRoleId(Long userRoleId);

    Page<DUserRoleList> findByPageable(Long userRoleId, String numberIdentity, String role, Pageable pageable);

    List<String> getRoles();
}
