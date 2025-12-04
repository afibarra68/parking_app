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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@Validated
@Log4j2
@RequestMapping("/auth")
public class UserLoginController {

    private final UserService service;


    public UserLoginController(UserService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public DUserLoginResponse login(@Valid @RequestBody DUserLogin rUserLoginRequest,
                                    HttpServletResponse httpResponse) throws WbException {
        return service.login(rUserLoginRequest, httpResponse);
    }

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
