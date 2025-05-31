package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.webstore.usersMs.dtos.DUserRole;
import com.webstore.usersMs.entities.UserRole;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface UserRoleMapper {

    UserRole fromDto(DUserRole dto);

    DUserRole toDto(UserRole dto);

    UserRole merge(DUserRole dto, @MappingTarget UserRole role);


}

