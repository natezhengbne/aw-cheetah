package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfoDto createUser(UserInfoDto userInfoDto) {
        UserEntity userFromDB = userRepository.save(mapInfoDtoToModel(userInfoDto));
        return mapModelToInfoDto(userFromDB);
    }

    public Boolean ifEmailExists(UserInfoDto userInfoDto) {
        if (userRepository.existsUserEntityByEmailEquals(userInfoDto.getEmail()) == false) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public Optional<UserEntity> findUserByEmail(String email) {
        return userRepository.findUserEntityByEmailIgnoreCase(email);
    }

    public boolean emailExists(String email) {
        return findUserByEmail(email).isPresent();
    }

    public UserEntity mapInfoDtoToModel(UserInfoDto userInfoDto) {
        return UserEntity.builder()
                .name(userInfoDto.getName())
                .email(userInfoDto.getEmail().toLowerCase())
                .title(userInfoDto.getTitle())
                .password("password12345")
                .status(Status.UNVERIFIED)
                .createdTime(OffsetDateTime.now())
                .updatedTime(OffsetDateTime.now()).build();
    }

    public UserInfoDto mapModelToInfoDto(UserEntity userEntity) {
        return UserInfoDto.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail().toLowerCase())
                .title(userEntity.getTitle())
                .password("password12345").build();
    }
}
