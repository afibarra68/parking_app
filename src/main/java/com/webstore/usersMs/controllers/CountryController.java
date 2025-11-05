package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DClient;
import com.webstore.usersMs.dtos.DCountry;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.ClientService;
import com.webstore.usersMs.services.CountryService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/countries")
public class CountryController {

    private final CountryService service;

    @PostMapping
    public DCountry create(@RequestBody DCountry client) throws WbException {
        return service.create(client);
    }

    @PutMapping
    public DCountry update(@RequestBody DCountry client) throws WbException {
        return service.update(client);
    }

    @GetMapping
    public List<DCountry> getClients(
            @RequestParam(required = false) Long countryId,
            @RequestParam(required = false) String description) throws WbException {
        return service.getBy(countryId, description);
    }

}
