package com.asyncworking.jwt;

import com.asyncworking.auth.AwcheetahAuthenticationToken;
import com.asyncworking.auth.AwcheetahGrantedAuthority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

import static com.asyncworking.jwt.JwtClaims.AUTHORIZATION;
import static com.asyncworking.jwt.JwtClaims.AUTHORIZATION_TYPE;
import static com.asyncworking.jwt.JwtClaims.COMPANY_IDS;
import static com.asyncworking.jwt.JwtClaims.PROJECT_IDS;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenVerifyFilter extends OncePerRequestFilter {

    private final SecretKey secretKey;
    private final JwtService jwtService;

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String authorizationHeader = request.getHeader(AUTHORIZATION.value());
        if (authorizationHeader == null || !authorizationHeader.startsWith(AUTHORIZATION_TYPE.value())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.replace(AUTHORIZATION_TYPE.value(), "");
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Claims body = claimsJws.getBody();

        String email = body.getSubject();
        Set<AwcheetahGrantedAuthority> grantedAuthorities = jwtService.getAuthoritiesFromJwtBody(body);
        Set<Long> companyIds = jwtService.getIdSetFromJwtBody(body, COMPANY_IDS);
        Set<Long> projectIds = jwtService.getIdSetFromJwtBody(body, PROJECT_IDS);

        Authentication authentication = new AwcheetahAuthenticationToken(
                email,
                null,
                grantedAuthorities,
                companyIds,
                projectIds
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

}
