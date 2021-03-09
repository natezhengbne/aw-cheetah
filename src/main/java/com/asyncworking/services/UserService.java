package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.Mapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

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
        String name = userRepository.findUserEntityByEmail(email).get().getName();
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

    public void createUserAndGenerateVerifyLink(UserInfoDto userInfoDto, String siteUrl) {
        UserEntity userEntity = mapper.mapInfoDtoToEntity(userInfoDto);
        userRepository.save(userEntity);
        this.generateVerifyLink(userInfoDto, siteUrl);
    }

    public String generateVerifyLink(UserInfoDto userInfoDto, String siteUrl) {
        String verifyLink = siteUrl + "/verify?code=" + this.generateJws(userInfoDto);
        log.info("verifyLink: {}", verifyLink);
        return verifyLink;
    }


    private String generateJws(UserInfoDto userInfoDto) {
        String jws = Jwts.builder()
                .setSubject("signUp")
                .claim("email", userInfoDto.getEmail())
                .signWith(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .compact();
        log.info("jwt token" + jws);

        return jws;
    }

    @Transactional
    public void verifyAccountAndActiveUser(String code) throws Exception{
        String email = this.decodedEmail(code);
        this.activeUser(email);
    }


    private String decodedEmail(String code) throws Exception{

            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(code);

            Claims body = jws.getBody();

            String email = body.get("email").toString();
            return email;
    }


    private void activeUser(String email) throws Exception{

        int numberOfActivatedUse = userRepository.updateStatusByEmail(email, Status.ACTIVATED);

        log.info("number of activated userEntity" + numberOfActivatedUse);

        if (numberOfActivatedUse == 0) {
            throw new NotFoundException("User not found");
        }

    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

}
