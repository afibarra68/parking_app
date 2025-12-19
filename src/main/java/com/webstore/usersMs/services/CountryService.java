package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DCountry;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CountryService {

    DCountry create(DCountry client) throws WbException;

    DCountry update(DCountry client) throws WbException;

    List<DCountry> getBy(Long countryId, String description) throws WbException;

    Page<DCountry> findByPageable(Long countryId, String description, String name, Pageable pageable) throws WbException;

    List<DCountry> findByQueryable(Long countryId, String description, String name) throws WbException;

    /**
     * Elimina un país por su ID.
     * Valida que el país no tenga relaciones con otras entidades (como Company).
     * 
     * @param countryId ID del país a eliminar
     * @throws WbException Si el país no existe o tiene relaciones con otras entidades
     */
    void delete(Long countryId) throws WbException;
}
