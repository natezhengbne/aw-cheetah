package com.asyncworking.repositories;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
public class UserRepositoryTest {
    UserEntity mockUser;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void insertMockEmp() {
        userRepository.deleteAll();
        when(passwordEncoder.encode("len123")).thenReturn("testpass");

        mockUser = UserEntity.builder()
                .id(1L)
                .name("Lengary")
                .email("a@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.UNVERIFIED)
                .password(passwordEncoder.encode("len123"))
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();


        userRepository.saveAndFlush(mockUser);
    }

    @Test
    public void shouldFindUserByEmail() {
        Optional<UserEntity> userEntity = userRepository.findUserEntityByEmail("a@asyncworking.com");
        assertEquals("testpass", userEntity.get().getPassword());
    }

    @Test
    public void shouldReturnEmptyDueToWrongEmail() {
        Optional<UserEntity> userEntity = userRepository.findUserEntityByEmail("b@asyncworking.com");
        assertTrue(userEntity.isEmpty());
    }



}
