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

    @Mappings({
            @Mapping(target = "appUserId", ignore = true),
            // Mapeo correcto según estructura de DB:
            // first_name (DB) -> firstName (primer nombre) ✓
            // last_name (DB) -> secondName (segundo nombre) - Entity secondName mapea a
            // last_name
            // second_name (DB) -> lastName (primer apellido) - Entity lastName mapea a
            // second_name
            // second_lastname (DB) -> secondLastname (segundo apellido) ✓
            @Mapping(source = "lastName", target = "lastName"), // DTO lastName (primer apellido) -> Entity lastName
                                                                // (DB: second_name)
            @Mapping(source = "secondName", target = "secondName"), // DTO secondName (segundo nombre) -> Entity
                                                                    // secondName (DB: last_name)
            @Mapping(source = "secondLastName", target = "secondLastname") // DTO secondLastName -> Entity
                                                                           // secondLastname (DB: second_lastname)
    })
    User fromDto(DUser dto);

    DUser toDto(User entity);

    @Mapping(target = "appUserId", source = "appUserId")
    DUserCreated toBasicData(User dto);

    UserLogin toLoginResponse(User dto);

    DUserLoginResponse tpDuserLoggin(UserLogin userRes);
}
