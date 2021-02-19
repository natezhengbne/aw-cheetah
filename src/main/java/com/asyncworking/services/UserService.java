package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfoDto createPassword(UserInfoDto userInfoDto) {
        UserEntity userFromDB = userRepository.save(mapInfoDtoToEntity(userInfoDto));

        return mapEntityToInfoDto(userFromDB);
    }

    private UserInfoDto mapEntityToInfoDto(UserEntity userEntity) {
        return UserInfoDto.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .password(userEntity.getPassword())
                .title(userEntity.getTitle())
                .build();
    }

    private UserEntity mapInfoDtoToEntity(UserInfoDto userInfoDto) {

//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
//        String encodePassword = encoder.encode(userInfoDto.getPassword());

        return UserEntity.builder()
                .name(userInfoDto.getName())
                .email(userInfoDto.getEmail())
                .password(userInfoDto.getPassword())

                .title(userInfoDto.getTitle())
                .status(Status.UNVERIFIED)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
//                .password(encodePassword)
                .build();
    }
}
