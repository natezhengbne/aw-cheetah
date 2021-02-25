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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
    public void shouldAddUserSuccessfullyGivenProperUserEntity() {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setName("Steven");
        userInfoDto.setEmail("skykk0128@gmail.com");

        UserEntity mockReturenedUserEntity = UserEntity.builder()
                .name("Steven")
                .email("skykk0128@gmail.com").build();
        when(userRepository.save(any())).thenReturn(mockReturenedUserEntity);
        UserInfoDto userInfoDtoGet = userService.createUser(userInfoDto);
        assertEquals("Steven", userInfoDtoGet.getName());
        assertEquals("skykk0128@gmail.com", userInfoDtoGet.getEmail());
    }

    @Test
    public void shouldFindEmailExistSuccessful(){
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setEmail("a@gmail.com");

        UserEntity mockReturenedUserEntity = UserEntity.builder()
                .email("a@gmail.com").build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockReturenedUserEntity));

        String email = userInfoDto.getEmail();
        boolean testEmail = userService.isEmailExist(email);

        assertTrue(testEmail);

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
