package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DUser;
import com.webstore.usersMs.dtos.DUserCreated;
import com.webstore.usersMs.dtos.DUserList;
import com.webstore.usersMs.dtos.DUserLogin;
import com.webstore.usersMs.dtos.DUserLoginResponse;
import com.webstore.usersMs.dtos.DTokenValidationResponse;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.model.UserLogin;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    DUserCreated create(DUser dUser) throws WbException;

    DUserCreated update(DUser dUser) throws WbException;

    DUserLoginResponse login(DUserLogin dUser, HttpServletResponse httpServletResponse) throws WbException;

    DUserCreated getUser(String numberIdentity) throws WbException;

    DUserCreated deleteByDocument(Long userDocument);

    Page<DUserList> findByPageable(Long appUserId, String numberIdentity, Long companyCompanyId, Pageable pageable);

    /**
     * Obtiene el usuario autenticado desde el SecurityContext.
     * 
     * Nota: El token JWT ya fue procesado y validado por el JwtRequestFilter
     * antes de que la petición llegue a los servicios. El SecurityContext
     * ya contiene el Authentication con el UserLogin establecido.
     * 
     * @return El UserLogin si está autenticado, null en caso contrario
     */
    UserLogin getAuthenticatedUser();

    /**
     * Valida un token JWT desde el header de la petición.
     * Este método valida el token manualmente sin depender del SecurityContext.
     * 
     * @param token El token JWT a validar
     * @return DTokenValidationResponse con el resultado de la validación
     */
    DTokenValidationResponse validateToken(String token);
}
