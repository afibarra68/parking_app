package com.webstore.usersMs.controllers;

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
import org.springframework.web.bind.annotation.PathVariable;

import com.webstore.usersMs.dtos.DClosedTransaction;
import com.webstore.usersMs.services.ClosedTransactionService;
import com.webstore.usersMs.error.WbException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/closed-transactions")
public class ClosedTransactionController {

    private final ClosedTransactionService service;

    @PostMapping
    public DClosedTransaction create(@RequestBody DClosedTransaction transaction) throws WbException {
        return service.create(transaction);
    }

    @PutMapping
    public DClosedTransaction update(@RequestBody DClosedTransaction transaction) throws WbException {
        return service.update(transaction);
    }

    @GetMapping
    public Page<DClosedTransaction> getClosedTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long companyCompanyId,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findBy(status, companyCompanyId, pageable);
    }

    @PostMapping("/close/{openTransactionId}")
    public DClosedTransaction closeTransaction(@PathVariable Long openTransactionId) throws WbException {
        return service.closeTransaction(openTransactionId);
    }
}

