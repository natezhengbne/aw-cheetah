package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Autowired
    UserService userService;

    @Test
    public void shouldAddUserSuccessfullyGivenProperUser() {
        UserInfoDto userPostDto = UserInfoDto.builder()
                .email("Leo7868@qq.com")
                .password("dddd1dd")
                .name("Leo")
                .build();

        UserEntity mockUserEntity = UserEntity.builder()
                .email("Leo7868@qq.com")
                .password("encodedPassword")
                .name("Leo")
                .id(1L)
                .status(Status.UNVERIFIED)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        when(userRepository.save(any())).thenReturn(mockUserEntity);

        assertEquals("Leo", mockUserEntity.getName());
        assertEquals("Leo7868@qq.com", mockUserEntity.getEmail());

    }
}
