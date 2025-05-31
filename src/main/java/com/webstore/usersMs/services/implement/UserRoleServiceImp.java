package com.webstore.usersMs.services.implement;

import static com.webstore.usersMs.error.handlers.enums.WbErrorCode.CLIENT_NOT_FOUND;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.dtos.DUserRole;
import com.webstore.usersMs.entities.UserRole;
import com.webstore.usersMs.mappers.UserRoleMapper;
import com.webstore.usersMs.repositories.UserRoleRepository;
import com.webstore.usersMs.services.UserRoleService;
import com.webstore.usersMs.error.WbException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserRoleServiceImp implements UserRoleService {

    private final UserRoleRepository repository;

    private final UserRoleMapper mapper = Mappers.getMapper(UserRoleMapper.class);

    @Override
    public DUserRole create(DUserRole client) throws WbException {
        return mapper.toDto(repository.save(mapper.fromDto(client)));
    }

    @Override
    public DUserRole update(DUserRole dto) throws WbException {
        Optional<UserRole> entity =  repository.findUserRole(dto.getUserId());
        if (entity.isEmpty()) {
            throw new WbException(CLIENT_NOT_FOUND);
        }
        UserRole userRole = entity.get();
        return mapper.toDto(repository.save(mapper.merge(dto, userRole )));
    }

}
