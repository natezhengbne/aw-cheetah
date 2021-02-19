package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserInfoDto createUser(UserInfoDto userInfoDto) {
        UserEntity userFromDB = userRepository.save(mapInfoDtoToEntity(userInfoDto));
        return mapEntityToInfoDto(userFromDB);
    }



    private UserInfoDto mapEntityToInfoDto(UserEntity userEntity) {
        return UserInfoDto.builder()
                .name(userEntity.getName())
                .password(userEntity.getPassword())
                .build();
    }

    private UserEntity mapInfoDtoToEntity(UserInfoDto userInfoDto) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        String encodePassword = encoder.encode(userInfoDto.getPassword());

        return UserEntity.builder()
                .name(userInfoDto.getName())
                .password(encodePassword)
                .build();
    }
}
