package com.asyncworking.services;

import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.UserInfoPostDto;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.UserMapper;
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

    private final UserMapper userMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public UserInfoPostDto login(String email, String password) {
        Optional<UserEntity> foundUserEntity = userRepository.findUserEntityByEmail(email);

        if (foundUserEntity.isEmpty()) {
            throw new UserNotFoundException("user not found");
        }

        String name = foundUserEntity.get().getName();
        log.debug(name);

        UserInfoPostDto userInfoPostDto = UserInfoPostDto.builder()
                .email(email)
                .name(name)
                .build();
        Authentication authenticate = this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        log.info(String.valueOf(authenticate));
        return userInfoPostDto;
    }

    public boolean ifEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void createUserAndGenerateVerifyLink(AccountDto accountDto, String siteUrl) {
        UserEntity userEntity = userMapper.mapInfoDtoToEntity(accountDto);
        userRepository.save(userEntity);
        this.generateVerifyLink(accountDto.getEmail(), siteUrl);
    }

    public String generateVerifyLink(String email, String siteUrl) {
        String verifyLink = siteUrl + "/verify?code=" + this.generateJws(email);
        log.info("verifyLink: {}", verifyLink);
        return verifyLink;
    }

    private String generateJws(String email) {
        String jws = Jwts.builder()
                .setSubject("signUp")
                .claim("email", email)
                .signWith(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .compact();
        log.info("jwt token" + jws);

        return jws;
    }

    @Transactional
    public Boolean isAccountActivated(String code) {
        String email = this.decodedEmail(code);
        int numberOfActiveUse = this.activeUser(email);

        log.debug("number of activated userEntity" + numberOfActiveUse);

        return numberOfActiveUse != 0;
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
        return userRepository.updateStatusByEmail(email, Status.ACTIVATED);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public boolean ifCompanyExits(String email) {
        return userRepository.findEmploymentByEmail(email).isPresent();
    }

    public boolean ifUnverified(String email) {
        return userRepository.findUnverifiedStatusByEmail(email).isPresent();
    }
}
