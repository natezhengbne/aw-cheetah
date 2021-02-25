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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    public void shouldAddUserSuccessfullyGivenProperUser() {
        UserInfoDto userPostDto = UserInfoDto.builder()
                .email("user@qq.com")
                .password("password")
                .name("user")
                .build();

        UserEntity mockUserEntity = UserEntity.builder()
                .email("user@qq.com")
                .password(bCryptPasswordEncoder.encode(userPostDto.getPassword()))
                .name("user")
                .id(1L)
                .status(Status.UNVERIFIED)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        when(userRepository.save(any())).thenReturn(mockUserEntity);
        UserInfoDto userInfoDtoGet = userService.createUser(userPostDto);

        assertEquals(userPostDto.getName(), userInfoDtoGet.getName());
        assertEquals(userPostDto.getEmail(), userInfoDtoGet.getEmail());

    }
}
