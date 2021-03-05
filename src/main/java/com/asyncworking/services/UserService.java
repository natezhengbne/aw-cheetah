package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.*;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final EmployeeRepository employeeRepository;

    private final AuthenticationManager authenticationManager;

    private final Mapper mapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Authentication login(String email, String password) {

        Authentication authenticate = this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        log.info(String.valueOf(authenticate));
        return authenticate;
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

        return body.get("email").toString();
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

    @Transactional
    public void createCompanyAndEmployee(UserInfoDto userInfoDto){

        UserEntity selectedUser = fetchUserByUserInfoDto(userInfoDto);
        log.info("selectedUser's email" + selectedUser.getEmail());
        Company newCompany = mapDtoToEntity(userInfoDto, selectedUser.getId());

        Company createdCompany = companyRepository.save(newCompany);

        Employee employee = Employee.builder()
                .id(new EmployeeId(selectedUser.getId(), createdCompany.getId()))
                .company(createdCompany)
                .userEntity(selectedUser)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        if (userInfoDto.getTitle() != null){
            employee.setTitle(userInfoDto.getTitle());
        }
        employeeRepository.saveAndFlush(employee);

    }

    private UserEntity fetchUserByUserInfoDto(UserInfoDto userInfoDto){
        return  userRepository.findUserEntityByEmail(userInfoDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("No such user!"));
    }

    private Company mapDtoToEntity(UserInfoDto userInfoDto, Long userId){
        return Company.builder()
                .name(userInfoDto.getCompany())
                .adminId(userId)
                .employees(new HashSet<>())
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

}
