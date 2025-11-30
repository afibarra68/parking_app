package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DCompanyBusinessService;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.CompanyBusinessServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/company-business-services")
public class CompanyBusinessServiceController {

    private final CompanyBusinessServiceInterface service;

    @PostMapping
    public DCompanyBusinessService create(@RequestBody DCompanyBusinessService companyBusinessService) throws WbException {
        return service.create(companyBusinessService);
    }

    @PutMapping
    public DCompanyBusinessService update(@RequestBody DCompanyBusinessService companyBusinessService) throws WbException {
        return service.update(companyBusinessService);
    }

    @GetMapping
    public List<DCompanyBusinessService> getCompanyBusinessServices(
            @RequestParam(required = false) Long companyBusinessServiceId,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long businessServiceId) throws WbException {
        return service.getBy(companyBusinessServiceId, companyId, businessServiceId);
    }

    @GetMapping("/by-company/{companyId}")
    public List<DCompanyBusinessService> getByCompanyId(@PathVariable Long companyId) {
        return service.getByCompanyId(companyId);
    }

    @DeleteMapping("/{companyBusinessServiceId}")
    public void delete(@PathVariable Long companyBusinessServiceId) throws WbException {
        service.delete(companyBusinessServiceId);
    }
}

