package com.asyncworking.services;

import com.asyncworking.config.FrontEndUrlConfig;
import com.asyncworking.constants.EmailType;
import com.asyncworking.constants.Status;
import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.EmployeeGetDto;
import com.asyncworking.dtos.ExternalEmployeeDto;
import com.asyncworking.dtos.InvitedAccountGetDto;
import com.asyncworking.dtos.InvitedAccountPostDto;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.exceptions.UserStatusUnexpectedException;
import com.asyncworking.jwt.JwtService;
import com.asyncworking.models.Company;
import com.asyncworking.models.Employee;
import com.asyncworking.models.EmployeeId;
import com.asyncworking.models.UserEntity;
import com.asyncworking.models.UserLoginInfo;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserLoginInfoRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.DateTimeUtility;
import com.asyncworking.utility.mapper.EmailMapper;
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
import java.util.List;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String EMAIL = "email";
    private static final String COMPANY_ID = "companyId";
    private static final String NAME = "name";
    private static final String TITLE = "title";

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailSendRepository emailSendRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;

    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final EmployeeMapper employeeMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final LinkGenerator linkGenerator;

    private final EmailMapper emailMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public boolean ifEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void createUserAndSendVerificationEmail(AccountDto accountDto) {
        UserEntity newUserEntity = userMapper.mapInfoDtoToEntity(accountDto);
        userRepository.save(newUserEntity);

        String userVerificationLink = linkGenerator.generateUserVerificationLink(
                newUserEntity.getEmail(),
                DateTimeUtility.MILLISECONDS_IN_DAY
        );

        emailService.sendLinkByEmail(emailMapper.toEmailContentDto(
                EmailType.Verification.toString(),
                userVerificationLink,
                newUserEntity
        ));
    }

    public void sendVerificationEmail(String email) {
        UserEntity unverifiedUserEntity = userRepository
                .findUnverifiedStatusByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot find unverified user with email: " + email));

        String userVerificationLink = linkGenerator.generateUserVerificationLink(
                unverifiedUserEntity.getEmail(),
                DateTimeUtility.MILLISECONDS_IN_DAY
        );

        emailService.sendLinkByEmail(emailMapper.toEmailContentDto(
                EmailType.Verification.toString(),
                userVerificationLink,
                unverifiedUserEntity
        ));
    }

    public void sendPasswordResetEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with id: " + email));

        if (userEntity.getStatus() == Status.UNVERIFIED) {
            throw new UserStatusUnexpectedException(Status.ACTIVATED, Status.UNVERIFIED);
        }

        String passwordRestLink = linkGenerator.generateResetPasswordLink(
                email,
                DateTimeUtility.MILLISECONDS_IN_DAY
        );

        emailService.sendLinkByEmail(emailMapper.toEmailContentDto(
                EmailType.Verification.toString(),
                passwordRestLink,
                userEntity
        ));
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

        String token = jwtService.createJwtToken(accountDto.getEmail());

        return userMapper.mapEntityToInvitedDto(returnedUser, token);
    }

    public ExternalEmployeeDto getUserInfo(String code) {
        ExternalEmployeeDto externalEmployeeDto = decodedInvitationLink(code);
        log.debug("User Info: " + externalEmployeeDto.toString());
        return externalEmployeeDto;
    }

    public EmployeeGetDto getResetterInfo(String code) {
        String email = decodedEmail(code);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with id: " + email));
        EmployeeGetDto userDto = employeeMapper.mapEntityToDto(user);
        return userDto;
    }

    private ExternalEmployeeDto decodedInvitationLink(String code) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(code);

        Claims body = jws.getBody();
        Long companyId = (long) Double.parseDouble(body.get(COMPANY_ID).toString());
        String companyName = getCompanyInfo(companyId).getName();
        return ExternalEmployeeDto.builder()
                .companyId(companyId)
                .email(body.get(EMAIL).toString())
                .name(body.get(NAME).toString())
                .title(body.get(TITLE).toString())
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

    public String isCompanyInvitationSuccess(String code) {
        Claims decodedBody;
        try {
            decodedBody = decode(code);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        String decodedEmail = decodedBody.get(EMAIL).toString();

        UserEntity userEntity = userRepository.findUserEntityByEmail(decodedEmail).get();

        Long userId = userEntity.getId();
        Long decodedCompanyId = Long.parseLong(decodedBody.get(COMPANY_ID).toString().replaceFirst(".0", ""));
        Company company = getCompanyInfo(decodedCompanyId);
        EmployeeId employeeId = buildEmployeeId(userId, company.getId());

        String decodedTitle = decodedBody.get(TITLE).toString();
        Employee employee = buildEmployee(employeeId, userEntity, company, decodedTitle);
        try {
            employeeRepository.save(employee);
            return Long.toString(decodedCompanyId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private EmployeeId buildEmployeeId(Long userId, Long companyId) {
        return EmployeeId.builder()
                .userId(userId)
                .companyId(companyId)
                .build();
    }

    private Employee buildEmployee(EmployeeId employeeId, UserEntity userEntity, Company company, String title) {
        return Employee.builder()
                .id(employeeId)
                .userEntity(userEntity)
                .company(company)
                .title(title)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
    }

    public Claims decode(String code) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(this.jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(code);
        Claims body = jws.getBody();
        return body;
    }

    private String decodedEmail(String code) {
        return decode(code).get(EMAIL).toString();
    }

    public int activeUser(String email) {
        return userRepository.updateStatusByEmail(email, Status.ACTIVATED);
    }

    public boolean ifCompanyExits(String email) {
        return userRepository.findEmploymentByEmail(email).isPresent();
    }

    public Long fetchCompanyId(String email) {
        List<Long> userCompanyIdList = userRepository.findUserCompanyIdList(email);
        UserEntity loginUserEntity = findUserByEmail(email);
        Company loginCompany = getCompanyInfo(userCompanyIdList.get(0));
        if (!userLoginInfoRepository.findUserLoginInfoByUserId(loginUserEntity.getId()).isPresent()) {
            UserLoginInfo userLoginInfo = creatUserLoginInfo(loginUserEntity.getId(), loginCompany.getId());
            userLoginInfoRepository.save(userLoginInfo);
        }
        return userLoginInfoRepository.findUserLoginCompanyIdByUserId(loginUserEntity.getId());
    }


    private UserLoginInfo creatUserLoginInfo(Long userId, Long companyId) {
        return UserLoginInfo.builder()
                .userId(userId)
                .companyId(companyId)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
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
