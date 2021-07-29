package com.asyncworking.auth;

import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class Guard {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectUserRepository projectUserRepository;

    //Check if the user belongs to the company
    public boolean checkCompanyId(Authentication authentication, Long companyId) {
        if (checkAnonymousAuthentication(authentication)) {
            log.info("Anonymous user, access denied");
            return false;
        }

        Optional<UserEntity> user = userRepository.findUserEntityByEmail(authentication.getName());
        Set<Long> companyIds = employeeRepository.findCompanyIdByUserId(user.get().getId());
        return companyIds.contains(companyId);
    }

    //Check if the user belongs to the project
    public boolean checkProjectId(Authentication authentication, Long projectId) {
        if (checkAnonymousAuthentication(authentication)) {
            log.info("Anonymous user, access denied");
            return false;
        }

        Optional<UserEntity> user = userRepository.findUserEntityByEmail(authentication.getName());

        Set<String> roleNames = userRoleRepository.findRoleIdByUserId(user.get().getId()).stream()
                .map(roleId -> roleRepository.findById(roleId).get().getName())
                .collect(Collectors.toSet());
        if (roleNames.contains("Company Manager")) {
            return true;
        }

        Set<Long> projectIds = projectUserRepository.findProjectIdByUserId(user.get().getId());
        return projectIds.contains(projectId);
    }

    public boolean checkProjectIdGetMethod(Authentication authentication, Long companyId, Long projectId) {
        if (checkAnonymousAuthentication(authentication)) {
            log.info("Anonymous user, access denied");
            return false;
        }

        //Check if the project belongs to the company
        Set<Long> projectIds = projectRepository.findProjectIdSetByCompanyId(companyId);
        if (!projectIds.contains(projectId)) {
            return false;
        }

        if (!checkCompanyId(authentication, companyId)) {
            log.info("User does not belong to this company!");
            return false;
        }

        if (!projectRepository.findById(projectId).get().getIsPrivate()) {
            return true;
        }

        return checkProjectId(authentication, projectId);
    }

    public boolean checkProjectIdOtherMethods(Authentication authentication, Long companyId, Long projectId) {
        if (checkAnonymousAuthentication(authentication)) {
            log.info("Anonymous user, access denied");
            return false;
        }

        if (!checkCompanyId(authentication, companyId)) {
            log.info("User does not belong to this company!");
            return false;
        }

        return checkProjectId(authentication, projectId);
    }

    public boolean checkAnonymousAuthentication(Authentication authentication) {
        return authentication.getPrincipal().equals("anonymousUser");
    }

}
