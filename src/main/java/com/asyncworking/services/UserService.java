package com.asyncworking.services;

import com.asyncworking.config.FrontEndUrlConfig;
import com.asyncworking.dtos.*;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    private final UserMapper userMapper;
    private final FrontEndUrlConfig frontEndUrlConfig;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.secretKey}")
    private String secretKey;

//    public UserInfoDto login(String email, String password) {
//        Optional<UserEntity> foundUserEntity = userRepository.findUserEntityByEmail(email);
//
//        if (foundUserEntity.isEmpty()) {
//            throw new UserNotFoundException("user not found");
//        }
//
//        String name = foundUserEntity.get().getName();
//        log.debug(name);
//
//        Long id = foundUserEntity.get().getId();
//
//        UserInfoDto userInfoDto = UserInfoDto.builder()
//                .id(id)
//                .email(email)
//                .name(name)
//                .build();
//        Authentication authenticate = this.authenticationManager
//                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
//        log.info(String.valueOf(authenticate));
//        return userInfoDto;
//    }

    public boolean ifEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void createUserAndGenerateVerifyLink(AccountDto accountDto) {
        UserEntity userEntity = userMapper.mapInfoDtoToEntity(accountDto);
        userRepository.save(userEntity);
        this.generateVerifyLink(accountDto.getEmail());
    }

    public InvitedAccountGetDto createUserViaInvitationLink(InvitedAccountPostDto accountDto) {
        Company company = companyRepository.findById(accountDto.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Cannot find company by id: " + accountDto.getCompanyId()));
        UserEntity userEntity = userMapper.mapInvitedDtoToEntityInvitation(accountDto);
        UserEntity returnedUser = userRepository.save(userEntity);
        EmployeeId employeeId = EmployeeId.builder()
                .userId(returnedUser.getId())
                .companyId(company.getId())
                .build();
        Employee employee = Employee.builder()
                .id(employeeId)
                .userEntity(returnedUser)
                .company(company)
                .title(accountDto.getTitle())
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        employeeRepository.save(employee);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("none");
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        String token = createJwtTokenForInvitationPeople(accountDto.getEmail(), authorities);

        return userMapper.mapEntityToInvitedDto(returnedUser, token);
    }

    public String createJwtTokenForInvitationPeople(String email,  List<GrantedAuthority> authorities) {
        return Jwts.builder()
                .setSubject(email)
                .claim("authorities", authorities)
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
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
                + "/invitations/info?code=" + this.encodeInvitation(companyId, email, name, title);
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

    public ExternalEmployeeDto getUserInfo(String code) {
        ExternalEmployeeDto externalEmployeeDto = decodedInvitationLink(code);
        log.debug("User Info: " + externalEmployeeDto.toString());
        return externalEmployeeDto;
    }

    private ExternalEmployeeDto decodedInvitationLink(String code) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(code);

        Claims body = jws.getBody();
        ExternalEmployeeDto externalEmployeeDto = ExternalEmployeeDto.builder()
                .companyId((long) Double.parseDouble(body.get("companyId").toString()))
                .email(body.get("email").toString())
                .name(body.get("name").toString())
                .title(body.get("title").toString())
                .build();
        return externalEmployeeDto;
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
