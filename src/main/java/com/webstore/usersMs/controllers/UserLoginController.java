package com.webstore.usersMs.controllers;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webstore.usersMs.dtos.DUserLogin;
import com.webstore.usersMs.dtos.DUserLoginResponse;
import com.webstore.usersMs.dtos.DTokenValidationResponse;
import com.webstore.usersMs.services.UserService;
import com.webstore.usersMs.error.WbException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@Validated
@Log4j2
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "API para autenticación y validación de tokens JWT")
public class UserLoginController {

    private final UserService service;


    public UserLoginController(UserService service) {
        this.service = service;
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = DUserLoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public DUserLoginResponse login(@Valid @RequestBody DUserLogin rUserLoginRequest,
                                    HttpServletResponse httpResponse) throws WbException {
        return service.login(rUserLoginRequest, httpResponse);
    }

    @Operation(summary = "Validar token", description = "Valida si un token JWT es válido y retorna información del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token válido",
                    content = @Content(schema = @Schema(implementation = DTokenValidationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @GetMapping("/validate")
    public DTokenValidationResponse validateToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }
        
        return service.validateToken(token);
    }

}
