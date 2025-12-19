package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DTicketTemplate;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.TicketTemplateInterface;
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
@RequestMapping(value = "/ticket-templates")
public class TicketTemplateController {

    private final TicketTemplateInterface service;

    @PostMapping
    public DTicketTemplate create(@RequestBody DTicketTemplate ticketTemplate) throws WbException {
        return service.create(ticketTemplate);
    }

    @PutMapping
    public DTicketTemplate update(@RequestBody DTicketTemplate ticketTemplate) throws WbException {
        return service.update(ticketTemplate);
    }

    @GetMapping
    public List<DTicketTemplate> getTicketTemplates(
            @RequestParam(required = false) Long ticketTemplateId,
            @RequestParam(required = false) String printerType,
            @RequestParam(required = false) String ticketType,
            @RequestParam(required = false) Long companyCompanyId,
            @RequestParam(required = false) Long userUserId) throws WbException {
        return service.getBy(ticketTemplateId, printerType, ticketType, companyCompanyId, userUserId);
    }

    @GetMapping("/pageable")
    public Page<DTicketTemplate> getTicketTemplatesPageable(
            @RequestParam(required = false) Long ticketTemplateId,
            @RequestParam(required = false) String printerType,
            @RequestParam(required = false) String ticketType,
            @RequestParam(required = false) Long companyCompanyId,
            @RequestParam(required = false) Long userUserId,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findByPageable(ticketTemplateId, printerType, ticketType, companyCompanyId, userUserId, pageable);
    }
}

