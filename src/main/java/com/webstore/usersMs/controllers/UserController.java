package com.webstore.usersMs.controllers;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webstore.usersMs.dtos.DUser;
import com.webstore.usersMs.dtos.DUserCreated;
import com.webstore.usersMs.services.UserService;
import com.webstore.usersMs.error.WbException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/users")
public class UserController {

    private final UserService service;

    @PostMapping("/create_public_user")
    public DUserCreated createUser(@Valid @RequestBody DUser dUser) throws WbException {
        return service.create(dUser);
    }

    @PostMapping("/down_public_user")
    public DUserCreated deleteUser(@RequestParam(required = true) Long userDocument) throws WbException {
        return service.deleteByDocument(userDocument);
    }

}

