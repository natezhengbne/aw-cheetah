package com.asyncworking.services;

import com.asyncworking.config.FrontEndUrlConfig;
import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.ExternalEmployeeDto;
import com.asyncworking.dtos.InvitedAccountPostDto;
import com.asyncworking.models.*;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserMapper userMapper;

    private UserService userService;

    @Autowired
    private FrontEndUrlConfig frontEndUrlConfig;

    @BeforeEach()
    void setup() {
        userService = new UserService(
                userRepository,
                companyRepository,
                employeeRepository,
                userMapper,
                frontEndUrlConfig);
        ReflectionTestUtils.setField(userService, "jwtSecret", "securesecuresecuresecuresecuresecuresecure");
        ReflectionTestUtils.setField(userService, "secretKey", "7756adfasdfenci,,@@33$$*()sdfsdkjhsnklp999002qejf\\\\//asdf");
    }

    @Test
    public void shouldFindEmailExistSuccessful() {
        AccountDto accountDto = AccountDto.builder()
                .email("a@qq.com")
                .build();

        UserEntity mockReturnedUserEntity = UserEntity.builder().email("a@gmail.com").build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockReturnedUserEntity));

        String email = accountDto.getEmail();
        assertTrue(userService.ifEmailExists(email));
    }

    @Test
    public void shouldFindUnverifiedUserSuccessful() {
        String email = "a@qq.com";

        UserEntity mockReturnedUserEntity = UserEntity.builder().email("a@gmail.com").status(Status.UNVERIFIED).build();
        when(userRepository.findUnverifiedStatusByEmail(any())).thenReturn(Optional.of(mockReturnedUserEntity));

        assertTrue(userService.ifUnverified(email));
    }

    @Test
    public void shouldFindUnverifiedUserUnsuccessfully() {
        String email = "a@qq.com";

        when(userRepository.findUnverifiedStatusByEmail(any())).thenReturn(Optional.empty());

        assertFalse(userService.ifUnverified(email));
    }


    @Test
    public void shouldGenerateInvitationLinkGivenDetail() {
        String siteUrl = frontEndUrlConfig.getFrontEndUrl();
        String invitationLink = userService.generateInvitationLink(1L, "user1@gmail.com", "user1", "developer");
        assertEquals(
                siteUrl.concat("/invitations/info?code=")
                        .concat("eyJhbGciOiJIUzI1NiJ9." +
                                "eyJzdWIiOiJpbnZpdGF0aW9uIiwiY29tcGFueUlkIjoxLCJlbWFpbCI6InVz" +
                                "ZXIxQGdtYWlsLmNvbSIsIm5hbWUiOiJ1c2VyMSIsInRpdGxlIjoiZGV2ZWxvcGVyIn0." +
                                "FsfFrxlLeCjcSBV1cWp6D_VstygnaSr9EWSqZKKX1dU"),
                invitationLink
        );
    }

    @Test
    public void shouldDecodeInvitationLink() {
        String code = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbnZpdGF0aW9uIiwiY29tcGFue" +
                "UlkIjoxLCJlbWFpbCI6InVzZXIxQGdtYWlsLmNvbSIsIm5hbWUiOiJ1c2VyMSIsI" +
                "nRpdGxlIjoiZGV2ZWxvcGVyIn0.FsfFrxlLeCjcSBV1cWp6D_VstygnaSr9EWSqZKKX1dU";

        Company company = Company.builder()
                .id(1L)
                .name("aw company")
                .description("aw company description")
                .website("asyncworking.com")
                .adminId(1L)
                .contactNumber("number")
                .contactEmail("aw@gmail.com")
                .industry("industry")
                .build();
        when(companyRepository.findById(1L)).thenReturn(Optional.ofNullable(company));
        ExternalEmployeeDto externalEmployeeDto = userService.getUserInfo(code);
        assertEquals("user1", externalEmployeeDto.getName());
        assertEquals("user1@gmail.com", externalEmployeeDto.getEmail());
        assertEquals("developer", externalEmployeeDto.getTitle());

    }

    @Test
    public void shouldGenerateActivationLinkGivenUserEmail() {
        String siteUrl = frontEndUrlConfig.getFrontEndUrl();
        String verifyLink = userService.generateVerifyLink("user0001@test.com");
        assertEquals(
                siteUrl.concat("/verifylink/verify?code=")
                        .concat("eyJhbGciOiJIUzI1NiJ9.")
                        .concat("eyJzdWIiOiJzaWduVXAiLCJlbWFpbCI6InVzZXIwMDAxQHRlc3QuY29tIn0.")
                        .concat("Lm7JlWoG0lyw2KWYBpnGfmt2HMP6H3vvPeN36gSVGrE"),
                verifyLink
        );
    }

    @Test
    public void shouldCreateUserAndGenerateActivationLinkGivenProperUserDto() {
        AccountDto accountDto = AccountDto.builder()
                .email("user@gmail.com")
                .password("len123")
                .name("user")
                .title("dev")
                .build();

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        userService.createUserAndGenerateVerifyLink(accountDto);

        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();
        assertEquals("user@gmail.com", savedUser.getEmail());
        assertEquals("user", savedUser.getName());
    }

    @Test
    public void shouldCreateNewUserViaInvitationLink() {
        InvitedAccountPostDto accountDto = InvitedAccountPostDto.builder()
                .name("Steven S Wang")
                .email("skykk0128@gmail.com")
                .password("password12345")
                .title("Dev")
                .companyId(1L)
                .build();
        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .email("plus@gmail.com")
                .name("aName")
                .build();
        Company company = Company.builder()
                .id(1L)
                .name("aw company")
                .description("aw company description")
                .website("asyncworking.com")
                .adminId(1L)
                .contactNumber("number")
                .contactEmail("aw@gmail.com")
                .industry("industry")
                .build();

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        ArgumentCaptor<Employee> captorEmployee = ArgumentCaptor.forClass(Employee.class);

        when(companyRepository.findById(any())).thenReturn(Optional.ofNullable(company));
        when(userRepository.save(any())).thenReturn(mockReturnedUserEntity);

        userService.createUserViaInvitationLink(accountDto);

        verify(employeeRepository).save(captorEmployee.capture());
        verify(userRepository).save(captor.capture());

        UserEntity savedUser = captor.getValue();
        assertEquals("skykk0128@gmail.com", savedUser.getEmail());
        assertEquals("Steven S Wang", savedUser.getName());
    }

    @Test
    public void shouldReturnTrueIfAccountActivated() {
        String code = "eyJhbGciOiJIUzI1NiJ9."
                .concat("eyJzdWIiOiJzaWduVXAiLCJlbWFpbCI6InVzZXJAZ21haWwuY29tIn0.")
                .concat("tC8BAIWlF8U5z5Ue-SPBZBxMUBqLwGeKbbLVCtMTmhw");

        when(userRepository.updateStatusByEmail("user@gmail.com", Status.ACTIVATED)).thenReturn(1);

        assertTrue(userService.isAccountActivated(code));
    }

    @Test
    public void throwExceptionWhenUserDatabaseWrong() {
        String expectedMessage = "database wrong";

        AccountDto accountDto = AccountDto.builder()
                .email("lengary@asyncworking.com")
                .title("VI")
                .build();

        when(userRepository.findByEmail(accountDto.getEmail()))
                .thenThrow(new RuntimeException(expectedMessage));

        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.ifEmailExists(accountDto.getEmail()));

        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldFindEmploymentExistSuccessful() {
        String email = "a@gmail.com";
        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .email("a@gmail.com").build();
        when(userRepository.findEmploymentByEmail(anyString()))
                .thenReturn(Optional.of(mockReturnedUserEntity));
        boolean testEmail = userService.ifCompanyExits(email);
        assertTrue(testEmail);
    }

    @Test
    public void getCompanyInfoWhenGivenUserEmail() {
        Long id = 1L;
        String email = "p@asyncworking.com";
        IEmployeeInfoImpl mockEmployeeInfo = IEmployeeInfoImpl.builder()
                .email(email)
                .name("p")
                .title("dev")
                .build();

        List<IEmployeeInfo> returnedEmployeeInfo = List.of(mockEmployeeInfo);

        when(userRepository.findAllEmployeeByCompanyId(id)).thenReturn(returnedEmployeeInfo);

        List<IEmployeeInfo> employeeInfo = userRepository.findAllEmployeeByCompanyId(id);
        assertEquals("p", employeeInfo.get(0).getName());
    }
}
