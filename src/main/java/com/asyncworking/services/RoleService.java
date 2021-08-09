package com.asyncworking.services;

import com.asyncworking.exceptions.RoleNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.RoleRepository;
import com.asyncworking.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public void assignRole(UserEntity user, RoleName roleName, Long targetId) {
        Role role = fetchRoleByName(roleName.value());
        UserRole userRole = UserRole.builder()
                .id(new UserRoleId(user.getId(), role.getId(), targetId))
                .userEntity(user)
                .role(role)
                .isAuthorized(true)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        userRoleRepository.save(userRole);
    }

    public Role fetchRoleByName(String roleName) {
        return roleRepository
                .findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName + " does not exist!"));
    }
}
