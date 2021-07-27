package com.asyncworking.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/refresh")
@RequiredArgsConstructor
public class JwtController {

    private final SecretKey secretKey;
    private final JwtService jwtService;

    @SneakyThrows
    @GetMapping
    public ResponseEntity refreshToken (@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        String email = body.getSubject();

        String newToken = jwtService.generateJwtToken(email);
        return ResponseEntity.ok(newToken);
    }
}
