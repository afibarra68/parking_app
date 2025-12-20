package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.webstore.usersMs.entities.enums.EBillingStatus;
import com.webstore.usersMs.entities.enums.ELargeVariableTicket;
import com.webstore.usersMs.entities.enums.EPrinterType;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import com.webstore.usersMs.utils.EnumDataMapper;
import com.webstore.usersMs.utils.EnumDataMapperUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DPrinter;
import com.webstore.usersMs.entities.Printer;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE,
        imports = {EnumDataMapperUtils.class, EnumDataMapper.class, EPrinterType.class, ELargeVariableTicket.class},
        uses = {EnumDataMapper.class})
public interface PrinterMapper {

    String PRINTER_EXP = "java(EnumDataMapperUtils.map(EPrinterType.class, dto.getPrinterType()))";

    String PAPER_SIZE_EXP = "java(EnumDataMapperUtils.map(ELargeVariableTicket.class, dto.getPaperType()))";

    String GET_PRINTER_TYPE = "java(EnumDataMapper.fromDto(entity.getPrinterType()))";

    String GET_PAPER_SIZE = "java(EnumDataMapper.fromDto(entity.getPaperType()))";

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "printerType", expression = PRINTER_EXP, resultType = EPrinterType.class)
    @Mapping(target = "paperType", expression = PAPER_SIZE_EXP, resultType = ELargeVariableTicket.class)
    Printer fromDto(DPrinter dto);

    @Mapping(target = "companyCompanyId", source = "company.companyId")
    @Mapping(target = "userUserId", source = "user.appUserId")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "printerType", expression = GET_PRINTER_TYPE)
    @Mapping(target = "paperType", expression = GET_PAPER_SIZE)
    DPrinter toDto(Printer entity);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "printerType", expression = PRINTER_EXP, resultType = EPrinterType.class)
    @Mapping(target = "paperType", expression = PAPER_SIZE_EXP, resultType = ELargeVariableTicket.class)
    Printer merge(DPrinter dto, @MappingTarget Printer printer);

    default List<DPrinter> toList(List<Printer> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DPrinter> toPage(Page<Printer> page) {
        return page.map(this::toDto);
    }

}

