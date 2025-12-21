package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DDataPrinting;
import com.webstore.usersMs.dtos.DMapField;
import com.webstore.usersMs.dtos.DOpenTransaction;
import com.webstore.usersMs.dtos.DTicketTemplate;
import com.webstore.usersMs.entities.enums.EReceiptModel;
import com.webstore.usersMs.error.WbException;

public interface TemplatePrinterService {

    DDataPrinting buildTicket(EReceiptModel eReceiptModel, DMapField fieldSet, DTicketTemplate template) throws WbException;
}

