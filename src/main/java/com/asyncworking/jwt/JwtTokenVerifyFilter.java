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

import static com.asyncworking.jwt.JwtClaims.*;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenVerifyFilter extends OncePerRequestFilter {

    private final SecretKey secretKey;

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        log.debug("doFilterInternal() started");
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

        Set<AwcheetahGrantedAuthority> grantedAuthorities = getAuthoritiesFromJwtBody(body);

        Set<Long> companyIds = getIdSetFromJwtBody(body, COMPANY_IDS.value());

        Set<Long> projectIds = getIdSetFromJwtBody(body, PROJECT_IDS.value());

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

    private Set<AwcheetahGrantedAuthority> getAuthoritiesFromJwtBody(Claims body) {
        var authorities = (List<LinkedTreeMap<String, Object>>) body.get(AUTHORITIES.value());
        return authorities.stream()
                .map(map -> new AwcheetahGrantedAuthority(map.get(ROLE.value()).toString(),
                        ((Double) map.get(TARGET_ID.value())).longValue()))
                .collect(Collectors.toSet());
    }

    private Set<Long> getIdSetFromJwtBody(Claims body, String idType) {
        var doubleIdSet =  (List<Double>) body.get(idType);
        return doubleIdSet.stream().
                map(Double::longValue)
                .collect(Collectors.toSet());
    }
}
