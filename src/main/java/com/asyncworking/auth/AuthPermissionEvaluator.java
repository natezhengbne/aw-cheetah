package com.asyncworking.auth;

import com.asyncworking.jwt.AwGrantedAuthority;
import com.asyncworking.models.Role;
import com.asyncworking.repositories.RoleRepository;
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

    private final RoleRepository roleRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || !(targetDomainObject instanceof Long) || !(permission instanceof String)){
            return false;
        }
        Long targetId = Long.valueOf(targetDomainObject.toString());
        Set<AwGrantedAuthority> authorities =  authentication.getAuthorities().stream()
                .map(grantedAuthority -> (AwGrantedAuthority) grantedAuthority)
                .collect(Collectors.toSet());
        for(AwGrantedAuthority authority : authorities) {
            if(authority.getTargetId() == targetId) {
                Role role = roleRepository.findByName(authority.getAuthority()).get();
                Set<String> permissions = role.getAuthorities().stream()
                        .map(authority1 -> authority1.getName())
                        .collect(Collectors.toSet());
                return permissions.contains(permission.toString());
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
