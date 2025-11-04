package com.webstore.usersMs.mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.webstore.usersMs.model.UserLogin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.webstore.usersMs.dtos.DUser;
import com.webstore.usersMs.dtos.DUserCreated;
import com.webstore.usersMs.dtos.DUserLoginResponse;
import com.webstore.usersMs.entities.User;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface UserMapper {

    @Mappings({@Mapping(target = "appUserId", ignore = true)})
    User fromDto(DUser dto);
    //@formatter:on

    DUser toDto(User dto);

    @Mapping(target = "appUserId", source = "appUserId")
    DUserCreated toBasicData(User dto);

    UserLogin toLoginResponse(User dto);

    DUserLoginResponse tpDuserLoggin(UserLogin userRes);
}

