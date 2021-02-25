package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;

import com.asyncworking.utils.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final Mapper mapper;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Authentication login(String email, String password) {

        Authentication authenticate = this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        log.info(String.valueOf(authenticate));
        return authenticate;
    }

    public boolean isEmailExist(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    public UserInfoDto createUser(UserInfoDto userInfoDto) {

        UserEntity userFromDB = userRepository.save(mapper.mapInfoDtoToEntity(userInfoDto));

        log.info("email: " + userFromDB.getEmail());
        log.info("name: " + userFromDB.getName());
        log.info("encoded password: " + userFromDB.getPassword());

        return mapper.mapEntityToInfoDto(userFromDB);
    }

}
