package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public Optional<UserEntity> findUserByEmail(UserInfoDto userInfoDto) {
        return userRepository.findUserEntityByEmailIgnoreCase(userInfoDto.getEmail());
    }

    public UserEntity mapInfoDtoToModel(UserInfoDto userInfoDto) {
//        UserEntity userEntity = new UserEntity();
//        userEntity.setName(userInfoDto.getName());
//        userEntity.setEmail(userInfoDto.getEmail());
//        List<UserEntity> userEntityList = new ArrayList<>();
//        for (UserEntity userEntity: userEntityList) {
//            if (userEntity.getName().equals(userInfoDto.getEmail())) {
//                return null;
//            }
//        }
        return UserEntity.builder()
                .name(userInfoDto.getName())
                .email(userInfoDto.getEmail())
                .title(userInfoDto.getTitle())
                .password("password12345")
                .status(Status.UNVERIFIED)
                .createdTime(OffsetDateTime.now())
                .updatedTime(OffsetDateTime.now()).build();
    }

    public UserInfoDto mapModelToInfoDto(UserEntity userEntity) {
//        UserInfoDto userInfoDto = new UserInfoDto();
//        userInfoDto.setName(userEntity.getName());
//        userInfoDto.setEmail(userEntity.getEmail());
        return UserInfoDto.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .title(userEntity.getTitle())
                .password("password12345").build();
    }
}
