package com.webstore.usersMs.mappers;

import com.webstore.usersMs.dtos.DClient;
import com.webstore.usersMs.dtos.DCountry;
import com.webstore.usersMs.entities.Client;
import com.webstore.usersMs.entities.Country;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface CountryMapper {

    Country fromDto(DCountry dto);

    DCountry toDto(Country entity);

    Country merge(DCountry dto, @MappingTarget Country client);

    default List<DCountry> toList(List<Country> list){
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

}

