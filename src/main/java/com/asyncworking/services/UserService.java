package com.asyncworking.services;

import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.Mapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final Mapper mapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public UserInfoDto login(String email, String password) {
        Optional<UserEntity> foundUserEntity = userRepository.findUserEntityByEmail(email);

        if (foundUserEntity.isEmpty()){
            throw new UserNotFoundException("user not found");
        }

        String name = foundUserEntity.get().getName();
        log.debug(name);

        UserInfoDto userInfoDto = UserInfoDto.builder()
                .email(email)
                .name(name)
                .build();
        Authentication authenticate = this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        log.info(String.valueOf(authenticate));
        return userInfoDto;
    }

    public boolean ifEmailExists(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    public void createUserAndGenerateVerifyLink(AccountDto accountDto, String siteUrl) {
        UserEntity userEntity = mapper.mapInfoDtoToEntity(accountDto);
        userRepository.save(userEntity);
        this.generateVerifyLink(accountDto, siteUrl);
    }

    public String generateVerifyLink(AccountDto accountDto, String siteUrl) {
        String verifyLink = siteUrl + "/verify?code=" + this.generateJws(accountDto);
        log.info("verifyLink: {}", verifyLink);
        return verifyLink;
    }

    private String generateJws(AccountDto accountDto) {
        String jws = Jwts.builder()
                .setSubject("signUp")
                .claim("email", accountDto.getEmail())
                .signWith(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .compact();
        log.info("jwt token" + jws);

        return jws;
    }

    @Transactional
    public void verifyAccountAndActiveUser(String code) {
        String email = this.decodedEmail(code);
        int numberOfActiveUse = this.activeUser(email);

        log.debug("number of activated userEntity" + numberOfActiveUse);

        if (numberOfActiveUse == 0) {
            throw new UserNotFoundException("Can not found user by email:" + email);
        }

    }

    private String decodedEmail(String code) {

            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(code);

            Claims body = jws.getBody();

        return body.get("email").toString();
    }

    private int activeUser(String email) {
        int numberOfActivatedUse = userRepository.updateStatusByEmail(email, Status.ACTIVATED);
        return numberOfActivatedUse;
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
    
    public boolean ifCompanyExits(String email){
        return userRepository.findEmploymentByEmail(email).isPresent();
    }
}
