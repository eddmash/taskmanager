package com.eddmash.app.authentication.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.eddmash.app.authentication.exception.AuthenticationException;
import com.eddmash.app.authentication.model.User;
import com.eddmash.app.shared.config.AppConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    @Getter
    private final AppConfig appConfig;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(User userDetails) {
        try {
            return generateToken(new HashMap<>(), userDetails);
        } catch (Exception e) {
            log.error("Token Generation failed: " + e.getMessage(), e);
            throw new AuthenticationException("Error occureed login");
        }
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Generate a token from the details of the signed in user, which gets signed
     * with the provided secret key.
     * 
     * @param extraClaims
     * @param userDetails
     * @return
     */
    private String generateToken(Map<String, Object> extraClaims, User userDetails) {
        return Jwts.builder().setClaims(extraClaims)
                .setSubject(userDetails.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // expire
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    /**
     * Create a signing key
     * 
     * @return
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(getAppConfig().getAuth().getTokenKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
