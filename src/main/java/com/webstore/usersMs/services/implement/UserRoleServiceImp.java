package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DUserCreated;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.entities.enums.ERole;
import com.webstore.usersMs.services.UserService;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.dtos.DUserRole;
import com.webstore.usersMs.entities.UserRole;
import com.webstore.usersMs.mappers.UserRoleMapper;
import com.webstore.usersMs.repositories.UserRoleRepository;
import com.webstore.usersMs.services.UserRoleService;
import com.webstore.usersMs.error.WbException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import javax.validation.constraints.NotNull;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserRoleServiceImp implements UserRoleService {

    private final UserRoleRepository repository;

    private final UserService userService;

    private final UserRoleMapper mapper = Mappers.getMapper(UserRoleMapper.class);

    @Override
    public void create(DUserRole role) throws WbException {
        DUserCreated user = userService.getUser(role.getNumberIdentity());

        UserRole roleCreated =  UserRole
                .builder()
                .role(ERole.valueOf(role.getRole()))
                .user(User
                        .builder()
                        .appUserId(user.getAppUserId())
                        .build())
                .build();
        log.info( "{}, {}", roleCreated, user.getAppUserId());

        repository.save(roleCreated);
    }

    @Override
    public void deleteByRoleId(@NotNull Long userRoleId) {
        repository.deleteById(userRoleId);
    }

}
