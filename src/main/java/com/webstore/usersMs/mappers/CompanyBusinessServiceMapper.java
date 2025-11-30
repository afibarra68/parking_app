package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.webstore.usersMs.dtos.DCompanyBusinessService;
import com.webstore.usersMs.entities.CompanyBusinessService;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE, uses = {CompanyMapper.class, BusinessServiceMapper.class})
public interface CompanyBusinessServiceMapper {

    @Mapping(target = "company", source = "company")
    @Mapping(target = "businessService", source = "businessService")
    CompanyBusinessService fromDto(DCompanyBusinessService dto);

    @Mapping(target = "company", source = "company")
    @Mapping(target = "businessService", source = "businessService")
    DCompanyBusinessService toDto(CompanyBusinessService entity);

    @Mapping(target = "company", source = "company")
    @Mapping(target = "businessService", source = "businessService")
    CompanyBusinessService merge(DCompanyBusinessService dto, @MappingTarget CompanyBusinessService companyBusinessService);

    default List<DCompanyBusinessService> toList(List<CompanyBusinessService> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

}

