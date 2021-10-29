package com.asyncworking.services;

import com.asyncworking.config.FrontEndUrlConfig;
import com.asyncworking.constants.EmailType;
import com.asyncworking.constants.Status;
import com.asyncworking.dtos.*;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.jwt.JwtService;
import com.asyncworking.models.Company;
import com.asyncworking.models.Employee;
import com.asyncworking.models.EmployeeId;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.EmployeeMapper;
import com.asyncworking.utility.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Date;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailSendRepository emailSendRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final EmployeeMapper employeeMapper;
    private final FrontEndUrlConfig frontEndUrlConfig;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public boolean ifEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void createUserAndSendMessageToSQS(AccountDto accountDto) {
        //Expires in 1 day
        Date expireDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        UserEntity userEntity = userMapper.mapInfoDtoToEntity(accountDto);
        emailService.saveEmailSendingRecord(userEntity, EmailType.Verification, accountDto.getEmail());
        userRepository.save(userEntity);
        emailService.sendMessageToSQS(userEntity, generateLink(userEntity.getEmail(),
                        "/verifylink/verify?code=",
                        "signUp", expireDate),
                EmailType.Verification, userEntity.getEmail());
    }

    private Company getCompanyInfo(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Cannot find company by id: " + companyId));
    }

    public InvitedAccountGetDto createUserViaInvitationLink(InvitedAccountPostDto accountDto) {
        Company company = getCompanyInfo(accountDto.getCompanyId());
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

        String token = jwtService.creatJwtToken(accountDto.getEmail());

        return userMapper.mapEntityToInvitedDto(returnedUser, token);
    }

//    public String createJwtTokenForInvitationPeople(String email,  List<GrantedAuthority> authorities) {
//        return Jwts.builder()
//                .setSubject(email)
//                .claim("authorities", authorities)
//                .setIssuedAt(new Date())
//                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
//                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
//                .compact();
//    }

    public void resendMessageToSQS(String email, EmailType emailType) {
        //Expires in 1 day
        Date expireDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        UserEntity emailSender = findUnVerifiedUserByEmail(email);
        emailService.saveEmailSendingRecord(emailSender, EmailType.Verification, email);
        emailService.sendMessageToSQS(emailSender,
                generateLink(email, "/verifylink/verify?code=", "signUp", expireDate),
                emailType, emailSender.getEmail());
    }

    private UserEntity findUnVerifiedUserByEmail(String email) {
        return userRepository.findUnverifiedStatusByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot find unverified user with email: " + email));
    }

    public String generateLink(String email, String subApi, String subject, Date expireDate) {
        String link = frontEndUrlConfig.getFrontEndUrl() + subApi + this.generateJws(email, subject, expireDate);
        log.info(subApi + "Link: {}", link);
        return link;
    }

    public void generateResetPasswordLink(String email) {
        Date expireDate = new Date(System.currentTimeMillis() + 1000 * 60 * 10); //Expires in 10 minutes
        String resetPasswordLink = generateLink(email, "/reset-password?code=", "reset-password", expireDate);
        UserEntity userEntity = findUserByEmail(email);
        emailService.saveEmailSendingRecord(userEntity, EmailType.ForgetPassword, email);
        emailService.sendMessageToSQS(userEntity, resetPasswordLink, EmailType.ForgetPassword, userEntity.getEmail());
    }

    private String generateJws(String email, String subject, Date expireDate) {
        String jws = Jwts.builder()
                .setSubject(subject)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
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

    public EmployeeGetDto getResetterInfo(String code) {
        String email = decodedEmail(code);
        UserEntity user = findUserByEmail(email);
        EmployeeGetDto userDto = employeeMapper.mapEntityToDto(user);
        return userDto;
    }

    private ExternalEmployeeDto decodedInvitationLink(String code) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(code);

        Claims body = jws.getBody();
        Long companyId = (long) Double.parseDouble(body.get("companyId").toString());
        String companyName = getCompanyInfo(companyId).getName();
        return ExternalEmployeeDto.builder()
                .companyId(companyId)
                .email(body.get("email").toString())
                .name(body.get("name").toString())
                .title(body.get("title").toString())
                .companyName(companyName)
                .build();
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

    public int activeUser(String email) {
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

    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with id: " + email));
    }

    @Transactional(rollbackFor = UserNotFoundException.class)
    public void updateEmailSent(String email) throws UserNotFoundException {
        if (emailSendRepository.updateVerificationEmailSent(email, OffsetDateTime.now(UTC)) < 1) {
            throw new UserNotFoundException("Cannot find user with email: " + email);
        }
    }

    @Transactional(rollbackFor = UserNotFoundException.class)
    public void resetPassword(UserInfoDto userInfoDto) {
        EmployeeGetDto userDto = getResetterInfo(userInfoDto.getAccessToken());
        String encodedPassword = passwordEncoder.encode(userInfoDto.getPassword());
        userRepository.resetPasswordById(userDto.getEmail(), encodedPassword, OffsetDateTime.now(UTC));
    }
}
