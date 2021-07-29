package com.asyncworking.repositories;


import com.asyncworking.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class UserRoleRepositoryTest extends DBHelper {
    private UserEntity mockUser;
    private Role mockRole1;
    private Role mockRole2;
    private UserRole mockUserRole1;
    private UserRole mockUserRole2;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void createMockData() {
        clearDb();
        when(passwordEncoder.encode("len123")).thenReturn("testpass");

        mockUser = UserEntity.builder()
                .name("user")
                .email("a@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.ACTIVATED)
                .password(passwordEncoder.encode("len123"))
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        mockRole1 = Role.builder()
                .name("Company Manager")
                .authorities(new HashSet<>())
                .build();

        mockRole2 = Role.builder()
                .name("Project Manager")
                .authorities(new HashSet<>())
                .build();


        userRepository.save(mockUser);
        roleRepository.save(mockRole1);
        roleRepository.save(mockRole2);

        mockUserRole1 = UserRole.builder()
                .id(new UserRoleId(mockUser.getId(), mockRole1.getId()))
                .userEntity(mockUser)
                .role(mockRole1)
                .build();

        mockUserRole2 = UserRole.builder()
                .id(new UserRoleId(mockUser.getId(), mockRole2.getId()))
                .userEntity(mockUser)
                .role(mockRole2)
                .build();
        userRoleRepository.save(mockUserRole1);
        userRoleRepository.save(mockUserRole2);
    }

    @Test
    public void shouldReturnRoleSetGivenUserID() {
        Set<Role> roleSet = userRoleRepository.findRoleSetByUserId(mockUser.getId());
        assertEquals(roleSet, Set.of(mockRole1, mockRole2));
    }
}
