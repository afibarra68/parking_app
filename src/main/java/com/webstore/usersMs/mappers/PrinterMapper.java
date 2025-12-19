package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DPrinter;
import com.webstore.usersMs.entities.Printer;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface PrinterMapper {

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    Printer fromDto(DPrinter dto);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "userUserId", source = "user.appUserId")
    DPrinter toDto(Printer entity);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "user", ignore = true)
    Printer merge(DPrinter dto, @MappingTarget Printer printer);

    default List<DPrinter> toList(List<Printer> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DPrinter> toPage(Page<Printer> page) {
        return page.map(this::toDto);
    }

}

