package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DUserPrinter;
import com.webstore.usersMs.entities.UserPrinter;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface UserPrinterMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "printer", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    UserPrinter fromDto(DUserPrinter dto);

    @Mapping(target = "userUserId", source = "user.appUserId")
    @Mapping(target = "printerPrinterId", source = "printer.printerId")
    DUserPrinter toDto(UserPrinter entity);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "printer", ignore = true)
    UserPrinter merge(DUserPrinter dto, @MappingTarget UserPrinter userPrinter);

    default List<DUserPrinter> toList(List<UserPrinter> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DUserPrinter> toPage(Page<UserPrinter> page) {
        return page.map(this::toDto);
    }

}

