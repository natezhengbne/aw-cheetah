package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserInfoDto createUser(UserInfoDto userInfoDto) {
        UserEntity userFromDB = userRepository.save(mapInfoDtoToModel(userInfoDto));
        return mapModelToInfoDto(userFromDB);
    }

    public UserEntity findUserByEmail(UserInfoDto userInfoDto) {
        return userRepository.findByEmailLike(userInfoDto.getEmail());
    }

    public UserEntity mapInfoDtoToModel(UserInfoDto userInfoDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userInfoDto.getName());
        userEntity.setEmail(userInfoDto.getEmail());
        return userEntity;
    }

    public UserInfoDto mapModelToInfoDto(UserEntity userEntity) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setId(userEntity.getId());
        userInfoDto.setName(userEntity.getName());
        userInfoDto.setEmail(userEntity.getEmail());
        return userInfoDto;
    }
}
