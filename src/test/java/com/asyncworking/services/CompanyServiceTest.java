package com.asyncworking.services;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.Company;
import com.asyncworking.models.Employee;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import javax.validation.constraints.AssertTrue;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
@AutoConfigureMockMvc
public class CompanyServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    Mapper mapper;

    @InjectMocks
    CompanyService companyService;


    @Test
    @Transactional
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

        String expectedMessage = "Can not found user by email";

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

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
        when(mapper.mapEntityToCompanyProfileDto(mockReturnedCompany))
                .thenReturn(companyModificationDto);

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

        String expectedMessage = "Can not found company with Id:2";

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


}
