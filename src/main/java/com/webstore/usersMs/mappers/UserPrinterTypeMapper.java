package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.webstore.usersMs.dtos.DUserPrinterType;
import com.webstore.usersMs.entities.UserPrinterType;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface UserPrinterTypeMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    UserPrinterType fromDto(DUserPrinterType dto);

    @Mapping(target = "userUserId", source = "user.appUserId")
    DUserPrinterType toDto(UserPrinterType entity);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserPrinterType merge(DUserPrinterType dto, @MappingTarget UserPrinterType userPrinterType);

    default List<DUserPrinterType> toList(List<UserPrinterType> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Page<DUserPrinterType> toPage(Page<UserPrinterType> page) {
        return page.map(this::toDto);
    }

}

