package com.asyncworking.jwt;

import com.asyncworking.auth.ApplicationUserService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;


@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecretKey secretKey;
    private final ApplicationUserService applicationUserService;

    public String generateJwtToken(String email) {
        UserDetails user = applicationUserService.loadUserByUsername(email);
        String jwtToken = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("authorities", user.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .signWith(secretKey)
                .compact();
        return jwtToken;
    }
}
