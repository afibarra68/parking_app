package com.webstore.usersMs.services.implement;

import com.webstore.usersMs.model.UserLogin;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.config.HashUtils;
import com.webstore.usersMs.config.JwtUtil;
import com.webstore.usersMs.dtos.DUser;
import com.webstore.usersMs.dtos.DUserCreated;
import com.webstore.usersMs.dtos.DUserLogin;
import com.webstore.usersMs.dtos.DUserLoginResponse;
import com.webstore.usersMs.entities.User;
import com.webstore.usersMs.mappers.UserMapper;
import com.webstore.usersMs.repositories.UserRepository;
import com.webstore.usersMs.repositories.UserRoleRepository;
import com.webstore.usersMs.services.UserService;
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

    private static final String EMPTY = "";

    private final UserRepository repository;

    private final JwtUtil serviceJWT;

    private final UserRoleRepository roleRepository;

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    public DUserCreated create(DUser dUser) throws WbException {

        Pair<String, String> passAndLead = HashUtils.getEncryptData(dUser.getPassword());
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

    ;

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
}
