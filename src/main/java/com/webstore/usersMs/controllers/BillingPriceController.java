package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DBillingPrice;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.BillingPriceService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping(value = "/billing-prices")
public class BillingPriceController {

    private final BillingPriceService service;

    @PostMapping
    public DBillingPrice create(@RequestBody DBillingPrice billingPrice) throws WbException {
        return service.create(billingPrice);
    }

    @PutMapping
    public DBillingPrice update(@RequestBody DBillingPrice billingPrice) throws WbException {
        return service.update(billingPrice);
    }

    @GetMapping
    public List<DBillingPrice> getBillingPrices(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long companyCompanyId,
            @RequestParam(required = false) String coverType) throws WbException {
        return service.getBy(status, companyCompanyId, coverType);
    }

    @GetMapping("/pageable")
    public Page<DBillingPrice> getBillingPricesPageable(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long companyCompanyId,
            @RequestParam(required = false) String coverType,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findByPageable(status, companyCompanyId, coverType, pageable);
    }
}

