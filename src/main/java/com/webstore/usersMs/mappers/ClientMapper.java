package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DClient;
import com.webstore.usersMs.entities.Client;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface ClientMapper {

    @Mapping(target = "paymentDay", ignore = true)
    Client fromDto(DClient dto);

    DClient toDto(Client entity);

    @Mapping(target = "paymentDay", ignore = true)
    Client merge(DClient dto, @MappingTarget Client client);

    default List<DClient> toList(List<Client> list){
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DClient> toPage(Page<Client> page) {
        return page.map(this::toDto);
    }
  
}

