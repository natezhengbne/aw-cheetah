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
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("No authorizationHeader or header not startWith Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.replace("Bearer ", "");

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        String username = body.getSubject();

        Set<AwcheetahGrantedAuthority> grantedAuthorities = getAuthoritiesFromJwtBody(body);

        Set<Long> companyIds = getIdSetFromJwtBody("companyIds", body);

        Set<Long> projectIds = getIdSetFromJwtBody("projectIds", body);

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
        var authorities = (List<LinkedTreeMap<String, Object>>) body.get("authorities");
        return authorities.stream()
                .map(map -> new AwcheetahGrantedAuthority(map.get("role").toString(), ((Double) map.get("targetId")).longValue()))
                .collect(Collectors.toSet());
    }

    private Set<Long> getIdSetFromJwtBody(String idType, Claims body) {
        var doubleIdSet =  (List<Double>) body.get(idType);
        return doubleIdSet.stream().
                map(Double::longValue)
                .collect(Collectors.toSet());
    }
}
