package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.model.UserLogin;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.config.HashUtils;
import com.webstore.usersMs.config.JwtUtil;
import com.webstore.usersMs.dtos.DUser;
import com.webstore.usersMs.dtos.DUserCreated;
import com.webstore.usersMs.dtos.DUserList;
import com.webstore.usersMs.dtos.DUserLogin;
import com.webstore.usersMs.dtos.DUserLoginResponse;
import com.webstore.usersMs.dtos.DTokenValidationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.mappers.UserMapper;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.repositories.UserRoleRepository;
import com.webstore.usersMs.repositories.CompanyRepository;
import com.webstore.usersMs.services.UserService;
import com.webstore.usersMs.entities.Company;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.error.handlers.enums.WbErrorCode;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository repository;

    private final JwtUtil serviceJWT;

    private final UserRoleRepository roleRepository;

    private final CompanyRepository companyRepository;

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    public DUserCreated create(DUser dUser) throws WbException {
        // Si no viene password, generar basado en numberIdentity
        String password = dUser.getPassword();
        if (password == null || password.trim().isEmpty()) {
            if (dUser.getNumberIdentity() == null || dUser.getNumberIdentity().trim().isEmpty()) {
                throw new WbException(WbErrorCode.CAN_NOT_CREATE_USER);
            }
            // Usar el numberIdentity como contraseña por defecto
            password = dUser.getNumberIdentity();
        }

        Pair<String, String> passAndLead = HashUtils.getEncryptData(password);
        User entity = mapper.fromDto(dUser);
        if (validateUser(entity)) {
            throw new WbException(WbErrorCode.CAN_NOT_CREATE_USER);
        }

        entity.setPassword(passAndLead.getLeft());
        entity.setSalt(passAndLead.getRight());

        return mapper.toBasicData(repository.save(entity));
    }

    public boolean validateUser(User user) {
        boolean acceptSaving = false;
        acceptSaving = repository.findByNumberIdentity(user.getNumberIdentity()).isPresent();
        return acceptSaving;
    }

    @Override
    public DUserCreated update(DUser dUser) throws WbException {
        if (dUser.getAppUserId() == null) {
            throw new WbException(WbErrorCode.NOT_FOUND);
        }

        Optional<User> existingUserOpt = repository.findById(dUser.getAppUserId());
        if (existingUserOpt.isEmpty()) {
            throw new WbException(WbErrorCode.NOT_FOUND);
        }

        User existingUser = existingUserOpt.get();

        // Actualizar campos permitidos
        // Mapeo correcto: DTO -> Entity (DB)
        // DTO firstName (primer nombre) -> Entity firstName (DB: first_name)
        if (dUser.getFirstName() != null) {
            existingUser.setFirstName(dUser.getFirstName());
        }
        // DTO secondName (segundo nombre) -> Entity secondName (DB: last_name)
        if (dUser.getSecondName() != null) {
            existingUser.setSecondName(dUser.getSecondName());
        }
        // DTO lastName (primer apellido) -> Entity lastName (DB: second_name)
        if (dUser.getLastName() != null) {
            existingUser.setLastName(dUser.getLastName());
        }
        // DTO secondLastName (segundo apellido) -> Entity secondLastname (DB:
        // second_lastname)
        if (dUser.getSecondLastName() != null) {
            existingUser.setSecondLastname(dUser.getSecondLastName());
        }
        if (dUser.getNumberIdentity() != null && !dUser.getNumberIdentity().equals(existingUser.getNumberIdentity())) {
            // Verificar que el nuevo numberIdentity no esté en uso por otro usuario
            Optional<User> userWithSameIdentity = repository.findByNumberIdentity(dUser.getNumberIdentity());
            if (userWithSameIdentity.isPresent()
                    && !userWithSameIdentity.get().getAppUserId().equals(existingUser.getAppUserId())) {
                throw new WbException(WbErrorCode.CAN_NOT_CREATE_USER);
            }
            existingUser.setNumberIdentity(dUser.getNumberIdentity());
        }
        if (dUser.getCompanyCompanyId() != null) {
            existingUser.setCompanyCompanyId(dUser.getCompanyCompanyId());
        }
        if (dUser.getProcessorId() != null && !dUser.getProcessorId().trim().isEmpty()) {
            existingUser.setProcessorId(dUser.getProcessorId());
        }
        if (dUser.getAccessCredential() != null) {
            existingUser.setAccessCredential(dUser.getAccessCredential());
        }

        // Si se proporciona una nueva contraseña, actualizarla
        if (dUser.getPassword() != null && !dUser.getPassword().trim().isEmpty()) {
            Pair<String, String> passAndLead = HashUtils.getEncryptData(dUser.getPassword());
            existingUser.setPassword(passAndLead.getLeft());
            existingUser.setSalt(passAndLead.getRight());
        }

        return mapper.toBasicData(repository.save(existingUser));
    }

    public DUserLoginResponse login(DUserLogin dUser, HttpServletResponse httpServletResponse)
            throws WbException {
        final User user = findUserBy(dUser.getUsername());

        String hashedPassword = HashUtils.getHashedText(dUser.getAccesKey(), user.getSalt());
        if (!hashedPassword.equals(user.getPassword())) {
            throw new WbException(WbErrorCode.INCORRECT_ACCESS);
        }
        Pair<String, Date> jwtPair = null;
        List<String> authorities = roleRepository.findByUser(user.getAppUserId()).stream()
                .map(userRole -> userRole.getRole().toString()).toList();
        UserLogin userRes = mapper.toLoginResponse(user);
        userRes.setRoles(authorities);

        // Obtener información de la empresa si el usuario está relacionado a una
        if (user.getCompanyCompanyId() != null) {
            Optional<Company> companyOpt = companyRepository.findByCompanyId(user.getCompanyCompanyId());
            if (companyOpt.isPresent()) {
                Company company = companyOpt.get();
                userRes.setCompanyId(company.getCompanyId());
                userRes.setCompanyName(company.getCompanyName());
                // Si la empresa tiene descripción, se puede agregar aquí
                // Por ahora, usamos el numberIdentity como descripción alternativa
                userRes.setCompanyDescription(company.getNumberIdentity());
                log.info("Compañía asignada al usuario en login: companyId={}, companyName={}",
                        company.getCompanyId(), company.getCompanyName());
            } else {
                log.warn("Usuario tiene companyCompanyId={} pero la compañía no existe en la BD",
                        user.getCompanyCompanyId());
            }
        } else {
            log.warn("Usuario sin compañía asociada. appUserId={}, numberIdentity={}",
                    user.getAppUserId(), user.getNumberIdentity());
        }

        jwtPair = serviceJWT.generateToken(userRes);
        userRes.setJwt(jwtPair.getLeft());
        userRes.setTokenType("Bearer");

        return mapper.tpDuserLoggin(userRes);
    }

    @Override
    public DUserCreated deleteByDocument(Long userDocument) {
        return null;
    }

    @Override
    public DUserCreated getUser(String numberIdentity) throws WbException {
        Optional<User> userOpt = repository.findByNumberIdentity(numberIdentity);

        if (userOpt.isPresent()) {
            return mapper.toBasicData(userOpt.get());

        }
        throw new WbException(WbErrorCode.NOT_FOUND);
    }

    private User findUserBy(String username) throws WbException {
        Optional<User> entityOptional = repository.findByNumberIdentity(username);
        if (entityOptional.isEmpty()) {
            throw new WbException(WbErrorCode.NOT_FOUND);
        }
        return entityOptional.get();
    }

    @Override
    public Page<DUserList> findByPageable(Long appUserId, String numberIdentity, Long companyCompanyId,
            Pageable pageable) {
        Page<User> users = repository.findByPageable(appUserId, numberIdentity, companyCompanyId, pageable);
        return users.map(user -> {
            DUserList.DUserListBuilder builder = DUserList.builder()
                    .appUserId(user.getAppUserId())
                    .firstName(user.getFirstName())
                    .secondName(user.getSecondName())
                    .lastName(user.getLastName())
                    .secondLastname(user.getSecondLastname())
                    .numberIdentity(user.getNumberIdentity())
                    .processorId(user.getProcessorId())
                    .sha(user.getSha())
                    .salt(user.getSalt())
                    .accessCredential(user.getAccessCredential())
                    .accessLevel(user.getAccessLevel())
                    .companyCompanyId(user.getCompanyCompanyId());

            // Obtener nombre de la compañía si existe
            if (user.getCompanyCompanyId() != null) {
                Optional<Company> company = companyRepository.findByCompanyId(user.getCompanyCompanyId());
                company.ifPresent(c -> builder.companyName(c.getCompanyName()));
            }

            return builder.build();
        });
    }

    @Override
    public UserLogin getAuthenticatedUser() {
        // El JwtRequestFilter ya procesó el token y estableció el Authentication
        // en el SecurityContext antes de que la petición llegue aquí
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserLogin) {
            UserLogin userLogin = (UserLogin) authentication.getPrincipal();
            log.debug("Usuario autenticado obtenido del SecurityContext: {}", userLogin.getAppUserId());
            return userLogin;
        }

        log.warn(
                "No se encontró usuario autenticado en el SecurityContext. El token puede no haber sido procesado correctamente.");
        return null;
    }

    @Override
    public DTokenValidationResponse validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return DTokenValidationResponse.builder()
                        .valid(false)
                        .message("Token no proporcionado")
                        .build();
            }

            // Validar el token usando JwtUtil
            try {
                Pair<io.jsonwebtoken.Claims, String> pair = serviceJWT.validateTokenFromString(token);

                // Verificar que el token no haya expirado
                io.jsonwebtoken.Claims claims = pair.getLeft();
                if (claims.getExpiration() != null && claims.getExpiration().before(new Date())) {
                    log.warn("Token expirado para usuario: {}", claims.getSubject());
                    return DTokenValidationResponse.builder()
                            .valid(false)
                            .message("Token expirado")
                            .build();
                }

                return DTokenValidationResponse.builder()
                        .valid(true)
                        .message("Token válido")
                        .build();

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                log.warn("Token expirado: {}", e.getMessage());
                return DTokenValidationResponse.builder()
                        .valid(false)
                        .message("Token expirado")
                        .build();
            } catch (io.jsonwebtoken.JwtException e) {
                log.warn("Token inválido: {}", e.getMessage());
                return DTokenValidationResponse.builder()
                        .valid(false)
                        .message("Token inválido: " + e.getMessage())
                        .build();
            }

        } catch (Exception e) {
            log.error("Error al validar token: {}", e.getMessage(), e);
            return DTokenValidationResponse.builder()
                    .valid(false)
                    .message("Error al validar token: " + e.getMessage())
                    .build();
        }
    }
}
