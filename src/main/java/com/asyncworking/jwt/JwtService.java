package com.asyncworking.jwt;

import com.asyncworking.auth.ApplicationUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecretKey secretKey;
    private final ApplicationUserService applicationUserService;

    public JwtDto refreshJwtToken(String auth) {
        String oldToken = auth.replace("Bearer ", "");

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(oldToken);
        Claims body = claimsJws.getBody();
        String email = body.getSubject();

        var authorities = (List<Map<String, String>>) body.get("authorities");
        Set<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                .map(map -> new SimpleGrantedAuthority(map.get("role")))
                .collect(Collectors.toSet());

        UserDetails user = applicationUserService.loadUserByUsername(email);
        if (grantedAuthorities.size() == user.getAuthorities().size()) {
            return JwtDto.builder()
                    .accessToken(oldToken)
                    .message("No need to refresh the jwtToken.")
                    .build();
        }
        String newToken = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("authorities", user.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .signWith(secretKey)
                .compact();
        return JwtDto.builder()
                .accessToken(newToken)
                .message("JwtToken has already refreshed.")
                .build();
    }
}
