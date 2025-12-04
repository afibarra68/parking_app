package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DClosedTransaction;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClosedTransactionService {

    DClosedTransaction create(DClosedTransaction dto) throws WbException;

    DClosedTransaction update(DClosedTransaction dto) throws WbException;

    Page<DClosedTransaction> findBy(String status, Long companyCompanyId, Pageable pageable);

    /**
     * Cierra una transacción abierta, calcula la tarifa y crea el registro en closed_transaction.
     * 
     * @param openTransactionId El ID de la transacción abierta a cerrar
     * @return La transacción cerrada creada
     */
    DClosedTransaction closeTransaction(Long openTransactionId) throws WbException;

    /**
     * Obtiene las estadísticas de transacciones cerradas del día actual para la compañía del usuario autenticado.
     * 
     * @return Estadísticas con total de transacciones, monto total y lista de transacciones (sin placa)
     */
    com.webstore.usersMs.dtos.DClosedTransactionStats getTodayStats() throws WbException;
}

