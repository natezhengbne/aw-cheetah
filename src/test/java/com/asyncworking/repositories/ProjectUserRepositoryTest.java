package com.asyncworking.repositories;

import com.asyncworking.models.*;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Set;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ProjectUserRepositoryTest extends DBHelper{
    private UserEntity mockUser;
    private Project mockProject1;
    private Project mockProject2;
    private ProjectUser mockProjectUser1;
    private ProjectUser mockProjectUser2;


    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void createMockData() {
        clearDb();
        when(passwordEncoder.encode("len123")).thenReturn("testpass");
        mockProject1 = Project.builder()
                .name("newProject")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        mockProject2 = Project.builder()
                .name("newProject2")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        mockUser = UserEntity.builder()
                .name("user")
                .email("a@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.ACTIVATED)
                .password(passwordEncoder.encode("len123"))
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();


        userRepository.save(mockUser);
        projectRepository.save(mockProject1);
        projectRepository.save(mockProject2);
    }

    @Test
    public void shouldReturnProjectIdSetGivenUserID() {
        mockProjectUser1 = ProjectUser.builder()
                .id(new ProjectUserId(mockProject1.getId(), mockUser.getId()))
                .userEntity(mockUser)
                .project(mockProject1)
                .attended(true)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        mockProjectUser2 = ProjectUser.builder()
                .id(new ProjectUserId(mockProject2.getId(), mockUser.getId()))
                .userEntity(mockUser)
                .project(mockProject2)
                .attended(true)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        projectUserRepository.save(mockProjectUser1);
        projectUserRepository.save(mockProjectUser2);
        Set<Long> projectSet = projectUserRepository.findProjectIdByUserId(mockUser.getId());
        assertEquals(projectSet, Set.of(mockProject1.getId(), mockProject2.getId()));
    }
}
