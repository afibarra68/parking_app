package com.webstore.usersMs.config;

import com.webstore.usersMs.model.UserLogin;
import com.webstore.usersMs.utils.Constants;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import com.webstore.usersMs.dtos.DUserLoginResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.webstore.usersMs.utils.Constants._APP_USER_ID_CLAIM;
import static com.webstore.usersMs.utils.Constants._AUTHORITIES_CLAIM;
import static com.webstore.usersMs.utils.Constants._COMPANY_ID_CLAIM;
import static com.webstore.usersMs.utils.Constants._COMPANY_NAME_CLAIM;
import static com.webstore.usersMs.utils.Constants._FIRST_NAME_CLAIM;
import static com.webstore.usersMs.utils.Constants._LAST_NAME_CLAIM;
import static com.webstore.usersMs.utils.Constants._ROLES_CLAIM;
import static com.webstore.usersMs.utils.Constants._SECOND_LAST_NAME_CLAIM;
import static com.webstore.usersMs.utils.Constants._SECOND_NAME_CLAIM;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtUtil {

    private static final String _BEARER = "Bearer ";

    private static final String _HEADER = "Authorization";

    @Value("${spring.application.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.application.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Value("${server.servlet.session.cookie.secure:true}")
    private boolean secure;

    @Value("${server.servlet.session.cookie.http-only:true}")
    private boolean httpOnly;

    public String extractUsername(String Jwt) {
        return extractClaim(Jwt, Claims::getSubject);
    }

    public <T> T extractClaim(String jwtSecret, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtSecret);
        return claimsResolver.apply(claims);
    }

    public Pair<String, Date> generateToken(UserLogin login) {
        Date expiryDate = new Date(new Date().getTime() + jwtExpirationInMs);
        JwtBuilder jwt = Jwts
                .builder()
                .setSubject(login.getNumberIdentity())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim(_FIRST_NAME_CLAIM, login.getFirstName())
                .claim(_SECOND_NAME_CLAIM, login.getSecondName())
                .claim(_LAST_NAME_CLAIM, login.getLastName())
                .claim(_SECOND_LAST_NAME_CLAIM, login.getSecondLastname())
                .claim(_COMPANY_NAME_CLAIM, login.getCompanyName())
                .claim(_COMPANY_ID_CLAIM, login.getCompanyId())
                .claim(_APP_USER_ID_CLAIM, login.getAppUserId())
                .claim(_ROLES_CLAIM,
                        login.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
                                Collectors.toList()))
                .signWith(getSingingKey(), SignatureAlgorithm.HS256);
        return new ImmutablePair<>(jwt.compact(), expiryDate);
    }

    public boolean istokenValid(String token, UserDetails userDetails) {
        final String firstName = extractUsername(token);
        return (firstName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean existsAuthJWT(HttpServletRequest request, HttpServletResponse res) {
        String authenticationHeader = request.getHeader(_HEADER);
        return  StringUtils.hasText(authenticationHeader) && authenticationHeader.startsWith(_BEARER);
    }

    public Pair<Claims, String> validateToken(HttpServletRequest request) {
        String jwtToken = getToken(request);
        return new ImmutablePair<>(Jwts
                .parserBuilder()
                .setSigningKey(new SecretKeySpec(Base64.getDecoder().decode(jwtSecret.getBytes()), HS256.getJcaName()))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody(), jwtToken);
    }

    public String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(_HEADER);
        if (hasText(bearerToken) && bearerToken.startsWith(_BEARER)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSingingKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    private Key getSingingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
