package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DUserPrinterType;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.UserPrinterTypeInterface;
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
@RequestMapping(value = "/user-printer-types")
public class UserPrinterTypeController {

    private final UserPrinterTypeInterface service;

    @PostMapping
    public DUserPrinterType create(@RequestBody DUserPrinterType userPrinterType) throws WbException {
        return service.create(userPrinterType);
    }

    @PutMapping
    public DUserPrinterType update(@RequestBody DUserPrinterType userPrinterType) throws WbException {
        return service.update(userPrinterType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws WbException {
        service.delete(id);
    }

    @GetMapping
    public List<DUserPrinterType> getUserPrinterTypes(
            @RequestParam(required = false) Long userPrinterTypeId,
            @RequestParam(required = false) Long userUserId,
            @RequestParam(required = false) String printerType,
            @RequestParam(required = false) Boolean isEnabled) throws WbException {
        return service.getBy(userPrinterTypeId, userUserId, printerType, isEnabled);
    }

    @GetMapping("/pageable")
    public Page<DUserPrinterType> getUserPrinterTypesPageable(
            @RequestParam(required = false) Long userPrinterTypeId,
            @RequestParam(required = false) Long userUserId,
            @RequestParam(required = false) String printerType,
            @RequestParam(required = false) Boolean isEnabled,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findByPageable(userPrinterTypeId, userUserId, printerType, isEnabled, pageable);
    }
}

