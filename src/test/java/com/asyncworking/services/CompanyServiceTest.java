package com.asyncworking.services;

import com.asyncworking.dtos.AvailableEmployeesGetDto;
import com.asyncworking.dtos.CompanyColleagueDto;
import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.CompanyMapper;
import com.asyncworking.utility.mapper.EmployeeMapper;
import com.asyncworking.utility.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private EmployeeMapper employeeMapper;


    private CompanyMapper companyMapper;


    private UserMapper userMapper;

    private CompanyService companyService;

    @BeforeEach()
    public void setup() {
        employeeMapper = new EmployeeMapper();
        companyMapper = new CompanyMapper();
        userMapper = new UserMapper(passwordEncoder);
        companyService = new CompanyService(
            userRepository,
            companyRepository,
            employeeRepository,
            companyMapper,
            userMapper,
            employeeMapper,
            roleService
        );
    }

    @Test
    public void createCompanyAndEmployeeGivenProperUserInfoDto() {
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .adminEmail("lengary@asyncworking.com")
                .name("AW")
                .userTitle("VI")
                .build();

        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .email("lengary@asyncworking.com")
                .name("ven").build();

        when(userRepository.findUserEntityByEmail(companyModificationDto.getAdminEmail()))
                .thenReturn(Optional.of(mockReturnedUserEntity));

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);

        companyService.createCompanyAndEmployee(companyModificationDto);

        verify(companyRepository).save(companyCaptor.capture());
        verify(employeeRepository).save(employeeCaptor.capture());
        Employee savedEmployee = employeeCaptor.getValue();
        Company savedCompany = companyCaptor.getValue();

        assertEquals("VI", savedEmployee.getTitle());
        assertEquals(mockReturnedUserEntity.getId(), savedCompany.getAdminId());
    }

    @Test
    public void throwNotFoundExceptionWhenUserNotExit() {
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .adminEmail("lengary@asyncworking.com")
                .name("AW")
                .userTitle("VI")
                .build();

        when(userRepository.findUserEntityByEmail(companyModificationDto.getAdminEmail()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class,
                () -> companyService.createCompanyAndEmployee(companyModificationDto));

        String expectedMessage = "Can not find user by email";

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getCompanyInfoWhenGivenUserEmail() {
        String email = "p@asyncworking.com";
        ICompanyInfoImpl mockCompanyInfo = ICompanyInfoImpl.builder()
                .companyId(1L)
                .name("p")
                .description("the description for + HQ")
                .build();

        List<ICompanyInfo> returnedCompanyInfo = List.of(mockCompanyInfo);

        when(companyRepository.findCompanyInfoByEmail(email)).thenReturn(returnedCompanyInfo);

        CompanyColleagueDto companyInfo = companyService.getCompanyInfoDto(email);
        assertEquals("p", companyInfo.getName());
        assertEquals(mockCompanyInfo.getDescription(), companyInfo.getDescription());
    }

    @Test
    void fetchCompanyProfileById() {
        Company mockReturnedCompany = Company.builder()
                .id(1L)
                .name("AW")
                .description("desc")
                .build();
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .companyId(1L)
                .name("AW")
                .description("desc")
                .build();

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(mockReturnedCompany));

        String expectedDescription = "desc";

        String actualDescription = companyService
                .fetchCompanyProfileById(1L)
                .getDescription();

        assertEquals(expectedDescription, actualDescription);
    }

    @Test
    void throwNotFoundExceptionWhenIdNotExist() {

        when(companyRepository.findById(2L))
                .thenReturn(Optional.empty());
        Exception exception = assertThrows(CompanyNotFoundException.class,
                () -> companyService.fetchCompanyProfileById(2L));

        String expectedMessage = "Can not find company with Id:2";

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldReturnAvailableEmployeesByCompanyIdAndProjectId() {
        AvailableEmployeesGetDto mockEmployeeGetDto = AvailableEmployeesGetDto.builder()
                .id(1L)
                .name("name1")
                .email("1@gmail.com")
                .title("dev")
                .build();
        IAvailableEmployeeInfo mockIEmployeeInfo = IAvailableEmployeeInfoImpl.builder()
                .id(1L)
                .name("name1")
                .title("dev")
                .email("1@gmail.com")
                .build();
        when(userRepository.findAvailableEmployeesByCompanyAndProjectId(1L, 1L))
                .thenReturn(List.of(mockIEmployeeInfo));
        List<AvailableEmployeesGetDto> result = companyService.findAvailableEmployees(1L, 1L);
        assertEquals(result.get(0).getName(), mockIEmployeeInfo.getName());
    }
}
