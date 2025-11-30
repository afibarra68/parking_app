package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DCountry;
import com.webstore.usersMs.error.WbException;

import java.util.List;

public interface CountryService {

    DCountry create(DCountry client) throws WbException;

    DCountry update(DCountry client) throws WbException;

    List<DCountry> getBy(Long countryId, String description) throws WbException;
}
