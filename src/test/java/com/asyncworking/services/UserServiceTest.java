package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void shouldAddUserSuccessfullyGivenProperUserEntity() {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setName("Steven");
        userInfoDto.setEmail("skykk0128@gmail.com");

        UserEntity mockReturenedUserEntity = new UserEntity();
        mockReturenedUserEntity.setName("Steven");
        mockReturenedUserEntity.setEmail("skykk0128@gmail.com");
        when(userRepository.save(any())).thenReturn(mockReturenedUserEntity);
        UserInfoDto userInfoDtoGet = userService.createUser(userInfoDto);
        assertEquals("Steven", userInfoDtoGet.getName());
        assertEquals("skykk0128@gmail.com", userInfoDtoGet.getEmail());
    }
}
