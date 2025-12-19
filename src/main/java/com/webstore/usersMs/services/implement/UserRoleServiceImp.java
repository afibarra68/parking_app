package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.dtos.DUserCreated;
import com.webstore.usersMs.dtos.DUserRole;
import com.webstore.usersMs.dtos.DUserRoleList;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.entities.enums.ERole;
import com.webstore.usersMs.services.UserService;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.entities.UserRole;
import com.webstore.usersMs.mappers.UserRoleMapper;
import com.webstore.usersMs.repositories.UserRoleRepository;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.services.UserRoleService;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.error.handlers.enums.WbErrorCode;
import com.webstore.usersMs.entities.Company;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import javax.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserRoleServiceImp implements UserRoleService {

    private final UserRoleRepository repository;

    private final UserService userService;

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

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
    public void update(Long userRoleId, DUserRole role) throws WbException {
        Optional<UserRole> existingRoleOpt = repository.findById(userRoleId);
        if (existingRoleOpt.isEmpty()) {
            throw new WbException(WbErrorCode.NOT_FOUND);
        }

        UserRole existingRole = existingRoleOpt.get();
        DUserCreated user = userService.getUser(role.getNumberIdentity());

        // Actualizar el rol y el usuario
        existingRole.setRole(ERole.valueOf(role.getRole()));
        existingRole.setUser(User
                .builder()
                .appUserId(user.getAppUserId())
                .build());

        log.info("Actualizando relación usuario-rol: {}, {}", userRoleId, user.getAppUserId());
        repository.save(existingRole);
    }

    @Override
    public void deleteByRoleId(@NotNull Long userRoleId) {
        repository.deleteById(userRoleId);
    }

    @Override
    public Page<DUserRoleList> findByPageable(Long userRoleId, String numberIdentity, String role, Pageable pageable) {
        // Obtener todas las relaciones usuario-rol
        List<UserRole> allRoles = repository.findAll();
        
        // Filtrar según los parámetros
        List<UserRole> filtered = allRoles.stream()
                .filter(ur -> userRoleId == null || ur.getUserRoleId().equals(userRoleId))
                .filter(ur -> numberIdentity == null || numberIdentity.isEmpty() || 
                        (ur.getUser() != null && ur.getUser().getNumberIdentity() != null && 
                         ur.getUser().getNumberIdentity().contains(numberIdentity)))
                .filter(ur -> role == null || role.isEmpty() || ur.getRole().name().equals(role))
                .collect(Collectors.toList());

        // Convertir a DTOs con información de usuario y compañía
        List<DUserRoleList> dtoList = filtered.stream().map(ur -> {
            User user = ur.getUser();
            DUserRoleList.DUserRoleListBuilder builder = DUserRoleList.builder()
                    .userRoleId(ur.getUserRoleId())
                    .role(ur.getRole().name());

            if (user != null) {
                builder.appUserId(user.getAppUserId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .secondName(user.getSecondName())
                        .secondLastname(user.getSecondLastname())
                        .numberIdentity(user.getNumberIdentity())
                        .companyCompanyId(user.getCompanyCompanyId());

                // Obtener nombre de la compañía
                if (user.getCompanyCompanyId() != null) {
                    Optional<Company> company = companyRepository.findByCompanyId(user.getCompanyCompanyId());
                    company.ifPresent(c -> builder.companyName(c.getCompanyName()));
                }
            }

            return builder.build();
        }).collect(Collectors.toList());

        // Crear página manualmente
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtoList.size());
        List<DUserRoleList> pageContent = dtoList.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, dtoList.size());
    }

    @Override
    public List<String> getRoles() {
        return Arrays.stream(ERole.values())
                .map(ERole::name)
                .collect(Collectors.toList());
    }

}
