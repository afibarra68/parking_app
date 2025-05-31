package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.webstore.usersMs.dtos.DClient;
import com.webstore.usersMs.entities.Client;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface ClientMapper {

    Client fromDto(DClient dto);

    DClient toDto(Client entity);

    Client merge(DClient dto, @MappingTarget Client client);

    default List<DClient> toList(List<Client> list){
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

}

