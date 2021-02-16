package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.User;
import com.asyncworking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserInfoDto createUser(UserInfoDto userInfoDto) {
        User userFromDB = userRepository.save(mapInfoDtoToModel(userInfoDto));
        return mapModelToInfoDto(userFromDB);
    }

    public User findUserByEmail(UserInfoDto userInfoDto) {

        return userRepository.findByEmailLike(userInfoDto.getEmail());
    }

    public User mapInfoDtoToModel(UserInfoDto userInfoDto) {
        User user = new User();
        user.setName(userInfoDto.getName());
        user.setEmail(userInfoDto.getEmail());
        return user;
    }

    public UserInfoDto mapModelToInfoDto(User user) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setId(user.getId());
        userInfoDto.setName(user.getName());
        userInfoDto.setEmail(user.getEmail());
        return userInfoDto;
    }
}
