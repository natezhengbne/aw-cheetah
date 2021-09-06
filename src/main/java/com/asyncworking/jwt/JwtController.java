package com.asyncworking.jwt;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refresh")
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;

    @SneakyThrows
    @GetMapping
    public ResponseEntity<JwtDto> refreshToken(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(jwtService.refreshJwtToken(auth));
    }
}
