package com.asyncworking.services;

import com.asyncworking.exceptions.RoleNotFoundException;
import com.asyncworking.models.Role;
import com.asyncworking.models.UserEntity;
import com.asyncworking.models.UserRole;
import com.asyncworking.models.UserRoleId;
import com.asyncworking.repositories.RoleRepository;
import com.asyncworking.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public void assignRole(UserEntity user, String roleName, Long targetId) {
        Role role = fetchRoleByName(roleName);
        UserRole userRole = UserRole.builder()
                .id(new UserRoleId(user.getId(), role.getId(), targetId))
                .userEntity(user)
                .role(role)
                .build();
        userRoleRepository.save(userRole);
    }

    public Role fetchRoleByName(String roleName) {
        return roleRepository
                .findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName + " does not exist!"));
    }
}
