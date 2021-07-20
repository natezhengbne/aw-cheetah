package com.asyncworking.auth;

import com.asyncworking.models.Employee;
import com.asyncworking.models.Role;
import com.asyncworking.models.UserEntity;
import com.asyncworking.models.ProjectUser;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Guard {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public boolean checkCompanyId(Authentication authentication, Long companyId) {
        Optional<UserEntity> user = userRepository.findUserEntityByEmail(authentication.getName());
        Set<Employee> employees = user.get().getEmployees();
        Set<Long> companyIds = employees.stream()
                .map(employee -> employee.getCompany().getId())
                .collect(Collectors.toSet());

        return companyIds.contains(companyId);
    }

    @SneakyThrows
    public boolean checkProjectIdGetMethod(Authentication authentication, Long projectId) {
        if (projectRepository.findById(projectId).get().getIsPrivate()) {
            return true;
        }

        return checkProjectId(authentication, projectId);
    }

    public boolean checkProjectId(Authentication authentication, Long projectId) {
        Optional<UserEntity> user = userRepository.findUserEntityByEmail(authentication.getName());
        Set<Role> roles = user.get().getRoles();
        Set<String> roleNames = roles.stream()
                .map(role -> role.getName())
                .collect((Collectors.toSet()));
        if (roleNames.contains("Company Owner")) {
            return true;
        }

        Set<ProjectUser> projectUsers = user.get().getProjectUsers();
        Set<Long> projectIds = projectUsers.stream()
                .map(projectUser -> projectUser.getProject().getId())
                .collect(Collectors.toSet());

        return projectIds.contains(projectId);
    }

}
