package br.com.project.springboot.starter.template.api.service;

import br.com.project.springboot.starter.template.api.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class TokenService {
    private static final int MIN_SECRET_KEY_BYTES = 64;

    @Value("${application.key.jwt.secret}")
    private String secretKey;
    @Value("${application.key.jwt.expiration}")
    private Integer validateTokenTime;

    @PostConstruct
    private void validateSecretKey() {
        if (Objects.isNull(secretKey) || secretKey.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_KEY_BYTES) {
            throw new IllegalStateException("KEY_JWT_SECRET deve ser definida com pelo menos 64 bytes (512 bits), "
                    + "requisito do algoritmo HS512. Gere um valor com: openssl rand -base64 64");
        }
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(Map<String, Object> extraClaims, User user) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validateTokenTime * 1000L))
                .signWith(getSecretKey(), Jwts.SIG.HS512)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
