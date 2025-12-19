package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DTicketTemplate;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketTemplateInterface {

    DTicketTemplate create(DTicketTemplate ticketTemplate) throws WbException;

    DTicketTemplate update(DTicketTemplate ticketTemplate) throws WbException;

    List<DTicketTemplate> getBy(Long ticketTemplateId, String printerType, String ticketType, Long companyCompanyId, Long userUserId) throws WbException;

    Page<DTicketTemplate> findByPageable(Long ticketTemplateId, String printerType, String ticketType, Long companyCompanyId, Long userUserId, Pageable pageable);

}

