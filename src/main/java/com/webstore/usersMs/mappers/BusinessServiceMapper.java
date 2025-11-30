package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DBusinessService;
import com.webstore.usersMs.entities.BusinessService;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface BusinessServiceMapper {

    BusinessService fromDto(DBusinessService dto);

    DBusinessService toDto(BusinessService entity);

    BusinessService merge(DBusinessService dto, @MappingTarget BusinessService businessService);

    default List<DBusinessService> toList(List<BusinessService> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DBusinessService> toPage(Page<BusinessService> page) {
        return page.map(this::toDto);
    }

}

