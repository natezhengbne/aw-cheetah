package com.asyncworking.auth;

import com.asyncworking.jwt.AwGrantedAuthority;
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

        Set<AwGrantedAuthority> authorities =  authentication.getAuthorities().stream()
                .map(grantedAuthority -> (AwGrantedAuthority) grantedAuthority)
                .collect(Collectors.toSet());

        for (AwGrantedAuthority authority : authorities) {
            if ( authority.getAuthority().equals(permission.toString())  && authority.getTargetId() == targetId) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
