package com.asyncworking.jwt;

import com.asyncworking.auth.AwcheetahGrantedAuthority;
import com.google.gson.internal.LinkedTreeMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import com.asyncworking.auth.AwcheetahAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenVerifyFilter extends OncePerRequestFilter {

    private final SecretKey secretKey;

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        log.debug("doFilterInternal() started");
        String authorizationHeader = request.getHeader(JwtComponent.AUTHORIZATION.value());
        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtComponent.AUTHORIZATION_TYPE.value())) {
            log.info("No authorizationHeader or header not startWith Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.replace(JwtComponent.AUTHORIZATION_TYPE.value(), "");

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        String username = body.getSubject();

        Set<AwcheetahGrantedAuthority> grantedAuthorities = getAuthoritiesFromJwtBody(body);

        Set<Long> companyIds = getIdSetFromJwtBody(JwtComponent.COMPANY_IDS.value(), body);

        Set<Long> projectIds = getIdSetFromJwtBody(JwtComponent.PROJECT_IDS.value(), body);

        Authentication authentication = new AwcheetahAuthenticationToken(
                username,
                null,
                grantedAuthorities,
                companyIds,
                projectIds
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private Set<AwcheetahGrantedAuthority> getAuthoritiesFromJwtBody(Claims body) {
        var authorities = (List<LinkedTreeMap<String, Object>>) body.get(JwtComponent.AUTHORITIES.value());
        return authorities.stream()
                .map(map -> new AwcheetahGrantedAuthority(map.get(JwtComponent.Role.value()).toString(),
                        ((Double) map.get(JwtComponent.TARGET_ID.value())).longValue()))
                .collect(Collectors.toSet());
    }

    private Set<Long> getIdSetFromJwtBody(String idType, Claims body) {
        var doubleIdSet =  (List<Double>) body.get(idType);
        return doubleIdSet.stream().
                map(Double::longValue)
                .collect(Collectors.toSet());
    }
}
