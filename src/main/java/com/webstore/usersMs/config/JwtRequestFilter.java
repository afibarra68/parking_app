package com.webstore.usersMs.config;

import com.webstore.usersMs.entities.enums.ERole;
import com.webstore.usersMs.error.handlers.enums.WbErrorCode;
import com.webstore.usersMs.model.UserLogin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;

import static com.webstore.usersMs.utils.Constants._APP_USER_ID_CLAIM;
import static com.webstore.usersMs.utils.Constants._COMPANY_ID_CLAIM;
import static com.webstore.usersMs.utils.Constants._COMPANY_NAME_CLAIM;
import static com.webstore.usersMs.utils.Constants._FIRST_NAME_CLAIM;
import static com.webstore.usersMs.utils.Constants._LAST_NAME_CLAIM;
import static com.webstore.usersMs.utils.Constants._ROLES_CLAIM;
import static com.webstore.usersMs.utils.Constants._SECOND_LAST_NAME_CLAIM;
import static com.webstore.usersMs.utils.Constants._SECOND_NAME_CLAIM;
import static java.time.ZoneId.systemDefault;

@Log4j2
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil util;

    public static final String _AUTHORITIES = "AUTHORITIES";


    public JwtRequestFilter(JwtUtil util) {
        this.util = util;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (util.existsAuthJWT(request, response)) {
                Pair<Claims, String> pair = util.validateToken(request);

                List<String> authorities = Optional.ofNullable(pair.getLeft())
                        .map(left -> left.get(_ROLES_CLAIM))
                        .filter(value -> value instanceof List<?>)
                        .map(value -> (List<?>) value)
                        .map(list -> list.stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .collect(Collectors.toList()))
                        .orElse(Collections.emptyList());

                if (!authorities.isEmpty()) {
                    setUpSpringAuthentication(pair.getLeft(), authorities, pair.getRight());
                } else {
                    log.warn("Token válido pero sin roles para el usuario: {}", pair.getLeft() != null ? pair.getLeft().getSubject() : "unknown");
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.error("Token expirado: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            log.error("Token inválido: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
        } catch (IllegalArgumentException e) {
            log.error("Error al procesar roles del token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Error al procesar roles: " + e.getMessage());
        } catch (AccessDeniedException e) {
            log.error("Acceso denegado: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, WbErrorCode.ACCESS_DENIED.getEnumKey());
        } catch (Exception e) {
            log.error("Error inesperado en el filtro JWT: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor");
        }
    }


    private void setUpSpringAuthentication(Claims claims, List<String> authorities, String jwt) {
        try {
            ArrayList<SimpleGrantedAuthority> collect = authorities
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isEmpty())
                    .map(s -> {
                        try {
                            return ERole.valueOf(s).getSplittedRole();
                        } catch (IllegalArgumentException e) {
                            log.warn("Rol inválido encontrado en el token: {}", s);
                            return Collections.<String>emptySet();
                        }
                    })
                    .filter(set -> !set.isEmpty())
                    .map(strings -> strings.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                    .collect(ArrayList::new, List::addAll, List::addAll);
            
            if (collect.isEmpty()) {
                log.warn("No se encontraron roles válidos para el usuario: {}", claims.getSubject());
            }
            
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(createAuthenticatedUser(claims, jwt), null, collect));
        } catch (Exception e) {
            log.error("Error al configurar la autenticación: {}", e.getMessage(), e);
            throw new RuntimeException("Error al configurar la autenticación", e);
        }
    }


    private UserLogin createAuthenticatedUser(Claims claims, String jwt) {
        Object rolesClaim = claims.get(_ROLES_CLAIM);
        List<String> roles = Collections.emptyList();
        if (rolesClaim instanceof List<?>) {
            roles = ((List<?>) rolesClaim).stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        
        // Convertir companyId de forma segura (puede venir como Integer o Long)
        Long companyId = null;
        Object companyIdClaim = claims.get(_COMPANY_ID_CLAIM);
        if (companyIdClaim != null) {
            if (companyIdClaim instanceof Long) {
                companyId = (Long) companyIdClaim;
            } else if (companyIdClaim instanceof Integer) {
                companyId = ((Integer) companyIdClaim).longValue();
            } else {
                companyId = Long.parseLong(String.valueOf(companyIdClaim));
            }
        }
        
        return UserLogin
                .builder()
                .numberIdentity(claims.getSubject())
                .jwtExpireDate(claims.getExpiration().toInstant().atZone(systemDefault()).toLocalDateTime())
                .appUserId(Long.parseLong(String.valueOf(claims.get(_APP_USER_ID_CLAIM))))
                .firstName((String) claims.get(_FIRST_NAME_CLAIM))
                .lastName((String) claims.get(_LAST_NAME_CLAIM))
                .secondName((String) claims.get(_SECOND_NAME_CLAIM))
                .secondLastname((String) claims.get(_SECOND_LAST_NAME_CLAIM))
                .companyId(companyId)
                .companyName((String) claims.get(_COMPANY_NAME_CLAIM))
                .companyDescription((String) claims.get(_COMPANY_NAME_CLAIM))
                .roles(roles)
                .jwt(jwt)
                .build();
    }

}


