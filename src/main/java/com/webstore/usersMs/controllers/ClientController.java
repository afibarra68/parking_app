package com.webstore.usersMs.controllers;

import org.apache.catalina.LifecycleState;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webstore.usersMs.dtos.DClient;
import com.webstore.usersMs.services.ClientService;
import com.webstore.usersMs.error.WbException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.awt.print.Pageable;
import java.util.List;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/client")
public class ClientController {

    private final ClientService service;

    @PostMapping
    public DClient create(@RequestBody DClient client) throws WbException {
        return service.create(client);
    }

    @PutMapping
    public DClient update(@RequestBody DClient client) throws WbException {
        return service.update(client);
    }

    @GetMapping
    public List<DClient> getClients(@RequestParam(required = false) String document) throws WbException {
        return service.findBy(document);
    }

}
