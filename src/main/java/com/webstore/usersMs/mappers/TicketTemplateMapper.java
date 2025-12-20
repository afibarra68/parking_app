package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DTicketTemplate;
import com.webstore.usersMs.entities.TicketTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface TicketTemplateMapper {

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "printer", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    TicketTemplate fromDto(DTicketTemplate dto);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "userUserId", source = "user.appUserId")
    @Mapping(target = "printerPrinterId", source = "printer.printerId")
    DTicketTemplate toDto(TicketTemplate entity);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "printer", ignore = true)
    TicketTemplate merge(DTicketTemplate dto, @MappingTarget TicketTemplate ticketTemplate);

    default List<DTicketTemplate> toList(List<TicketTemplate> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DTicketTemplate> toPage(Page<TicketTemplate> page) {
        return page.map(this::toDto);
    }

}

