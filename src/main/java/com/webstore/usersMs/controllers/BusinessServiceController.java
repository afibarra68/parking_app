package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DBusinessService;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.BusinessServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/business-services")
public class BusinessServiceController {

    private final BusinessServiceInterface service;

    @PostMapping
    public DBusinessService create(@RequestBody DBusinessService businessService) throws WbException {
        return service.create(businessService);
    }

    @PutMapping
    public DBusinessService update(@RequestBody DBusinessService businessService) throws WbException {
        return service.update(businessService);
    }

    @GetMapping
    public List<DBusinessService> getBusinessServices(
            @RequestParam(required = false) Long businessServiceId,
            @RequestParam(required = false) String principalName,
            @RequestParam(required = false) String code) throws WbException {
        return service.getBy(businessServiceId, principalName, code);
    }

    @GetMapping("/pageable")
    public Page<DBusinessService> getBusinessServicesPageable(
            @RequestParam(required = false) Long businessServiceId,
            @RequestParam(required = false) String principalName,
            @RequestParam(required = false) String code,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findByPageable(businessServiceId, principalName, code, pageable);
    }
}

