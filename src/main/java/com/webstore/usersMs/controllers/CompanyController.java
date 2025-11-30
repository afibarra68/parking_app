package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DCompany;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.CompanyService;
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
@RequestMapping(value = "/companies")
public class CompanyController {

    private final CompanyService service;

    @PostMapping
    public DCompany create(@RequestBody DCompany company) throws WbException {
        return service.create(company);
    }

    @PutMapping
    public DCompany update(@RequestBody DCompany company) throws WbException {
        return service.update(company);
    }

    @GetMapping
    public List<DCompany> getCompanies(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String numberIdentity) throws WbException {
        return service.getBy(companyId, companyName, numberIdentity);
    }

}

