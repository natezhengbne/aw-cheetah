package com.asyncworking.services;

import com.asyncworking.exceptions.RoleNotFoundException;
import com.asyncworking.models.Role;
import com.asyncworking.models.UserEntity;
import com.asyncworking.models.UserRole;
import com.asyncworking.repositories.RoleRepository;
import com.asyncworking.repositories.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    private RoleService roleService;

    private Role mockRole;

    private UserEntity mockUser;

    @BeforeEach
    public void setup() {
        roleService = new RoleService(
                roleRepository,
                userRoleRepository
        );

        mockRole = Role.builder()
                .id(1L)
                .name("Company Manager")
                .build();

        mockUser = UserEntity.builder()
                .id(1L)
                .build();
    }

//    @Test
//    public void shouldAssignRoleGivenRoleNameAndUserEntity() {
//        when(roleRepository.findByName(any())).thenReturn(Optional.of(mockRole));
//        ArgumentCaptor<UserRole> userRoleArgumentCaptor = ArgumentCaptor.forClass(UserRole.class);
//        roleService.assignRole(mockUser, "Company Manager");
//        verify(userRoleRepository).save(userRoleArgumentCaptor.capture());
//    }

    @Test
    public void shouldThrowRoleNotFoundExceptionGivenInvalidRoleName() {
        when(roleRepository.findByName("InvalidName")).thenThrow(
                new RoleNotFoundException("InvalidName does not exist!"));
        assertThrows(RoleNotFoundException.class, () -> roleService.fetchRoleByName("InvalidName"));
    }
}

