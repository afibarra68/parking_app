package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DUserRole;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.UserRoleService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/user_role")
public class UserRoleController {

    private final UserRoleService service;

    @PostMapping()
    public void createUseRole(@Valid @RequestBody DUserRole dUser) throws WbException {
        service.create(dUser);
    }

    @DeleteMapping("/{userRoleId}")
    public void deleteUserRole(@Valid @PathVariable Long userRoleId) throws WbException {
        service.deleteByRoleId(userRoleId);
    }

}

