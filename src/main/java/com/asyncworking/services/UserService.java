package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfoDto createUser(UserInfoDto userInfoDto) {
        UserEntity userFromDB = userRepository.save(mapInfoDtoToModel(userInfoDto));
        return mapModelToInfoDto(userFromDB);
    }

    public boolean isEmailExist(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    public UserEntity mapInfoDtoToModel(UserInfoDto userInfoDto) {
        return UserEntity.builder()
                .name(userInfoDto.getName())
                .email(userInfoDto.getEmail().toLowerCase())
                .password("password12345")
                .status(Status.UNVERIFIED)
                .createdTime(OffsetDateTime.now())
                .updatedTime(OffsetDateTime.now()).build();
    }

    public UserInfoDto mapModelToInfoDto(UserEntity userEntity) {
        return UserInfoDto.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail().toLowerCase())
                .password("password12345").build();
    }
}
