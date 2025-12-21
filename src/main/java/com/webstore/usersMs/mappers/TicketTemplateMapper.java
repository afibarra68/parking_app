package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DTicketTemplate;
import com.webstore.usersMs.entities.TicketTemplate;
import com.webstore.usersMs.entities.enums.EPrinterType;
import com.webstore.usersMs.entities.enums.ETicketType;
import com.webstore.usersMs.utils.EnumDataMapper;
import com.webstore.usersMs.utils.EnumDataMapperUtils;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE, imports = { EnumDataMapperUtils.class, ETicketType.class,
        EPrinterType.class, EnumDataMapper.class }, uses = { EnumDataMapper.class })
public interface TicketTemplateMapper {

    String TICKET_TYPE_EXP = "java(EnumDataMapperUtils.map(ETicketType.class, dto.getTicketType()))";

    String GET_TICKET_TYPE_EXP = "java(EnumDataMapper.fromDto(entity.getTicketType()))";

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "printer", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "ticketType", expression = TICKET_TYPE_EXP, resultType = ETicketType.class)
    TicketTemplate fromDto(DTicketTemplate dto);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "userUserId", source = "user.appUserId")
    @Mapping(target = "printerPrinterId", source = "printer.printerId")
    @Mapping(target = "ticketType", expression = GET_TICKET_TYPE_EXP)
    DTicketTemplate toDto(TicketTemplate entity);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "userUserId", source = "user.appUserId")
    @Mapping(target = "printerPrinterId", source = "printer.printerId")
    @Mapping(target = "printerName", source = "printer.printerName")
    @Mapping(target = "printerType", source = "printer.printerType", qualifiedByName = "printerTypeToString")
    @Mapping(target = "conectionString", source = "printer.connectionString")
    @Mapping(target = "ticketType", expression = GET_TICKET_TYPE_EXP)
    DTicketTemplate toDtoForPrinting(TicketTemplate entity);

    /**
     * Convierte EPrinterType enum a String
     */
    @Named("printerTypeToString")
    default String printerTypeToString(EPrinterType printerType) {
        return printerType != null ? printerType.name() : null;
    }

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "printer", ignore = true)
    @Mapping(target = "ticketType", expression = TICKET_TYPE_EXP, resultType = ETicketType.class)
    TicketTemplate merge(DTicketTemplate dto, @MappingTarget TicketTemplate ticketTemplate);

    default List<DTicketTemplate> toList(List<TicketTemplate> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DTicketTemplate> toPage(Page<TicketTemplate> page) {
        return page.map(this::toDto);
    }

}
