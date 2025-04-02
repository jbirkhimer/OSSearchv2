package edu.si.ossearch.security.jwt;

import edu.si.ossearch.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Base64;
import java.util.concurrent.Callable;
import javax.security.auth.Subject;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${ossearch.jwt.secret}")
    private String jwtSecret;

    @Value("${ossearch.jwt.expirationMs}")
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(UserDetailsImpl userPrincipal) {
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }

        return false;
    }

    // Method using Subject::callAs as recommended in Java 23 Security Developer's Guide
    public <T> T performActionAsSubject(Subject subject, Callable<T> action) throws Exception {
        return Subject.callAs(subject, action);
    }

    // Example of how to use the new method
    public String generateJwtTokenAsSubject(Subject subject, String username) throws Exception {
        return performActionAsSubject(subject, () -> generateTokenFromUsername(username));
    }

    // Method to get the current Subject
    public Subject getCurrentSubject() {
        return Subject.current();
    }
}