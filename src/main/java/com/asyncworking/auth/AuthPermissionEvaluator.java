package com.asyncworking.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        Long targetId = Long.valueOf(targetDomainObject.toString());

        Set<AwcheetahGrantedAuthority> authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> (AwcheetahGrantedAuthority) grantedAuthority)
                .collect(Collectors.toSet());

        return authorities.stream().anyMatch(authority ->
                authority.getAuthority().equals(permission.toString()) && authority.getTargetId().equals(targetId));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
