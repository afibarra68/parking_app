package com.webstore.usersMs.config;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
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
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

import javax.crypto.spec.SecretKeySpec;

@Service
public class JwtService {

    private static final String AUTHORITIES = "AUTHORITIES";

    private static final String USER_ID_CLAIM = "USER_ID_CLAIM";

    private static final String FULL_NAME_CLAIM = "FULL_NAME_CLAIM";

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

    public Pair<String, Date> generateToken(DUserLoginResponse dUserLogin) {
        Date expiryDate = new Date(new Date().getTime() + jwtExpirationInMs);
        JwtBuilder jwt = Jwts
            .builder()
            .setSubject(dUserLogin.getFistName())
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .claim("username", dUserLogin.getFistName())
            .claim("AUTHORITIES",
        dUserLogin.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
                    Collectors.toList()))
            .signWith(getSingingKey(), SignatureAlgorithm.HS256);
        return new ImmutablePair<>(jwt.compact(), expiryDate);
    }

    public boolean istokenValid(String token, UserDetails userDetails) {
        final String firstName = extractUsername(token);
        return  (firstName.equals(userDetails.getUsername())) && !isTokenExpired(token);
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
