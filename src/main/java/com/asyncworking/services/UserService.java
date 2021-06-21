package com.asyncworking.services;

import com.asyncworking.config.FrontEndUrlConfig;
import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.UserInfoDto;
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
    private final FrontEndUrlConfig frontEndUrlConfig;

    @Value("${jwt.secret}")
    private String jwtSecret;


    public UserInfoDto login(String email, String password) {
        Optional<UserEntity> foundUserEntity = userRepository.findUserEntityByEmail(email);

        if (foundUserEntity.isEmpty()) {
            throw new UserNotFoundException("user not found");
        }

        String name = foundUserEntity.get().getName();
        log.debug(name);

        Long id = foundUserEntity.get().getId();

        UserInfoDto userInfoDto = UserInfoDto.builder()
                .id(id)
                .email(email)
                .name(name)
                .build();
        Authentication authenticate = this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        log.info(String.valueOf(authenticate));
        return userInfoDto;
    }

    public boolean ifEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void createUserAndGenerateVerifyLink(AccountDto accountDto) {
        UserEntity userEntity = userMapper.mapInfoDtoToEntity(accountDto);
        userRepository.save(userEntity);
        this.generateVerifyLink(accountDto.getEmail());
    }

    public void createUserViaInvitationLink(AccountDto accountDto) {
        UserEntity userEntity = userMapper.mapInfoDtoToEntityInvitation(accountDto);
        userRepository.save(userEntity);
    }

    public String generateVerifyLink(String email) {
        String verifyLink = frontEndUrlConfig.getFrontEndUrl() + "/verifylink/verify?code=" + this.generateJws(email);
        log.info("verifyLink: {}", verifyLink);
        return verifyLink;
    }

    private String generateJws(String email) {
        String jws = Jwts.builder()
                .setSubject("signUp")
                .claim("email", email)
                .signWith(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .compact();
        log.info("jwt token: " + jws);

        return jws;
    }

    public String generateInvitationLink(Long companyId, String email, String name, String title) {
        String invitationLink = frontEndUrlConfig.getFrontEndUrl()
                + "/invitations/register?code=" + this.encodeInvitation(companyId, email, name, title);
        log.info("invitationLink: " + invitationLink);
        return invitationLink;
    }

    private String encodeInvitation(Long companyId, String email, String name, String title) {
        String invitationJwt = Jwts.builder()
                .setSubject("invitation")
                .claim("companyId", companyId)
                .claim("email", email)
                .claim("name", name)
                .claim("title", title)
                .signWith(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .compact();
        log.info("invitationJwt: " + invitationJwt);
        return invitationJwt;
    }

    private String decodedInvitationLink(String code) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(code);

        Claims body = jws.getBody();

        return body.get("companyId").toString() +
                body.get("email").toString() +
                body.get("name").toString() +
                body.get("title").toString();
    }

    @Transactional
    public Boolean isAccountActivated(String code) {
        String email = this.decodedEmail(code);
        int numberOfActiveUser = this.activeUser(email);

        log.debug("number of activated userEntity" + numberOfActiveUser);

        return numberOfActiveUser != 0;
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

    public boolean ifCompanyExits(String email) {
        return userRepository.findEmploymentByEmail(email).isPresent();
    }

    public Long fetchCompanyId(String email) {
        return userRepository.findEmployeesByEmail(email).get(0).getId().getCompanyId();
    }

    public boolean ifUnverified(String email) {
        return userRepository.findUnverifiedStatusByEmail(email).isPresent();
    }

    public UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with id: " + userId));
    }
}
