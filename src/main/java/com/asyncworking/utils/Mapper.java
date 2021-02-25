package com.asyncworking.utils;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RequiredArgsConstructor
public class Mapper {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserEntity mapInfoDtoToEntity(UserInfoDto userInfoDto) {
        String encodedPassword = bCryptPasswordEncoder.encode(userInfoDto.getPassword());
        return UserEntity.builder()
                .name(userInfoDto.getName())
                .email(userInfoDto.getEmail().toLowerCase())
                .password(encodedPassword)
                .status(Status.UNVERIFIED)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    public UserInfoDto mapEntityToInfoDto(UserEntity userEntity) {
        return UserInfoDto.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .build();
    }
}
