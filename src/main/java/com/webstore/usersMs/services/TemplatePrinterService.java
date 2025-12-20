package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DMapField;
import com.webstore.usersMs.dtos.DOpenTransaction;
import com.webstore.usersMs.dtos.DTicketTemplate;
import com.webstore.usersMs.entities.enums.EReceiptModel;
import com.webstore.usersMs.error.WbException;

public interface TemplatePrinterService {
    
    /**
     * Construye el ticket reemplazando los placeholders del template con los datos de la transacción
     * @param openTransaction La transacción abierta con todos sus datos
     * @param printerType El tipo de impresora (COM, WINDOWS, NETWORK)
     * @return El template procesado como string
     * @throws WbException Si no se encuentra el template o hay un error al procesarlo
     */
    String buildTicket(EReceiptModel eReceiptModel, DMapField fieldSet, DTicketTemplate template) throws WbException;
}

