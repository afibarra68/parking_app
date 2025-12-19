package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DUserPrinter;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.UserPrinterInterface;
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
@RequestMapping(value = "/user-printers")
public class UserPrinterController {

    private final UserPrinterInterface service;

    @PostMapping
    public DUserPrinter create(@RequestBody DUserPrinter userPrinter) throws WbException {
        return service.create(userPrinter);
    }

    @PutMapping
    public DUserPrinter update(@RequestBody DUserPrinter userPrinter) throws WbException {
        return service.update(userPrinter);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws WbException {
        service.delete(id);
    }

    @GetMapping
    public List<DUserPrinter> getUserPrinters(
            @RequestParam(required = false) Long userPrinterId,
            @RequestParam(required = false) Long userUserId,
            @RequestParam(required = false) Long printerPrinterId,
            @RequestParam(required = false) Boolean isActive) throws WbException {
        return service.getBy(userPrinterId, userUserId, printerPrinterId, isActive);
    }

    @GetMapping("/pageable")
    public Page<DUserPrinter> getUserPrintersPageable(
            @RequestParam(required = false) Long userPrinterId,
            @RequestParam(required = false) Long userUserId,
            @RequestParam(required = false) Long printerPrinterId,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findByPageable(userPrinterId, userUserId, printerPrinterId, isActive, pageable);
    }
}

