package com.webstore.usersMs.services.implement;

import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import com.webstore.usersMs.config.HashUtils;
import com.webstore.usersMs.config.JwtService;
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

    private final JwtService  serviceJWT;

    private final UserRoleRepository roleRepository;

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    public DUserCreated create(DUser dUser) throws WbException {
        Pair<String, String> passAndLead = HashUtils.getEncryptData(dUser.getAccesKey());
        User entity = mapper.fromDto(dUser);
        entity.setHashedPassword(passAndLead.getLeft());
        entity.setSalt(passAndLead.getRight());
        return mapper.toBasicData(repository.save(entity));
    }

    public DUserLoginResponse login(DUserLogin dUser, HttpServletResponse httpServletResponse)
        throws WbException {
        User user = findUserBy(dUser.getUsername());

        mapper.toLoginResponse(user);

        String hashedPassword = HashUtils.getHashedText(dUser.getAccesKey(), user.getSalt());
        if (!hashedPassword.equals(user.getHashedPassword())) {
            throw new WbException(WbErrorCode.INCORRECT_ACCESS);
        }
        Pair<String, Date> jwtPair = null;
        List<String> authorities = roleRepository.findByUser(user.getUserId()).stream()
            .map(userRole -> userRole.getRole().toString()).toList();
        DUserLoginResponse userRes = mapper.toLoginResponse(user);
        userRes.setRoles(authorities);
        jwtPair = serviceJWT.generateToken(userRes);
        userRes.setJwt(jwtPair.getLeft());
        userRes.setTokenType("BEARER");
        return userRes;
    }

    private User findUserBy(String username) throws WbException {
        Optional<User> entityOptional = repository.findByFirstName(username);
        if (entityOptional.isEmpty()) {
            throw new WbException(WbErrorCode.NOT_FOUND);
        }
        return entityOptional.get();
    }
}
