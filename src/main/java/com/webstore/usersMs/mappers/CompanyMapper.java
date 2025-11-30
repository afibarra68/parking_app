package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DCompany;
import com.webstore.usersMs.entities.Company;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE, uses = {CountryMapper.class}, imports = {CountryMapper.class})
public interface CompanyMapper {

    @Mapping(target = "country", source = "country")
    Company fromDto(DCompany dto);

    @Mapping(target = "country", source = "country")
    DCompany toDto(Company entity);

    Company merge(DCompany dto, @MappingTarget Company company);

    default List<DCompany> toList(List<Company> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DCompany> toPage(Page<Company> page) {
        return page.map(this::toDto);
    }

}

