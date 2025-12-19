package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DPrinter;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.PrinterInterface;
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
@RequestMapping(value = "/printers")
public class PrinterController {

    private final PrinterInterface service;

    @PostMapping
    public DPrinter create(@RequestBody DPrinter printer) throws WbException {
        return service.create(printer);
    }

    @PutMapping
    public DPrinter update(@RequestBody DPrinter printer) throws WbException {
        return service.update(printer);
    }

    @GetMapping
    public List<DPrinter> getPrinters(
            @RequestParam(required = false) Long printerId,
            @RequestParam(required = false) String printerName,
            @RequestParam(required = false) String printerType,
            @RequestParam(required = false) Long companyCompanyId,
            @RequestParam(required = false) Long userUserId,
            @RequestParam(required = false) Boolean isActive) throws WbException {
        return service.getBy(printerId, printerName, printerType, companyCompanyId, userUserId, isActive);
    }

    @GetMapping("/pageable")
    public Page<DPrinter> getPrintersPageable(
            @RequestParam(required = false) Long printerId,
            @RequestParam(required = false) String printerName,
            @RequestParam(required = false) String printerType,
            @RequestParam(required = false) Long companyCompanyId,
            @RequestParam(required = false) Long userUserId,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findByPageable(printerId, printerName, printerType, companyCompanyId, userUserId, isActive, pageable);
    }
}

