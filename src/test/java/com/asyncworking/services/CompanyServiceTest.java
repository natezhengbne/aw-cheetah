package com.asyncworking.services;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.Company;
import com.asyncworking.models.Employee;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import javax.validation.constraints.AssertTrue;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

    @InjectMocks
    CompanyService companyService;

    @Test
    @Transactional
    public void createCompanyAndEmployeeGivenProperUserInfoDto() {
        CompanyInfoDto companyInfoDto = CompanyInfoDto.builder()
                .adminEmail("lengary@asyncworking.com")
                .name("AW")
                .userTitle("VI")
                .build();

        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .email("lengary@asyncworking.com")
                .name("ven").build();

        when(userRepository.findUserEntityByEmail(companyInfoDto.getAdminEmail()))
                .thenReturn(Optional.of(mockReturnedUserEntity));

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        companyService.createCompanyAndEmployee(companyInfoDto);
        verify(companyRepository).save(companyCaptor.capture());
        verify(employeeRepository).save(employeeCaptor.capture());
        Employee savedEmployee = employeeCaptor.getValue();
        Company savedCompany = companyCaptor.getValue();

        assertEquals("VI", savedEmployee.getTitle());
        assertEquals(mockReturnedUserEntity.getId(), savedCompany.getAdminId());
    }

    @Test
    public void throwNotFoundExceptionWhenUserNotExit() {
        CompanyInfoDto companyInfoDto = CompanyInfoDto.builder()
                .adminEmail("lengary@asyncworking.com")
                .name("AW")
                .userTitle("VI")
                .build();

        when(userRepository.findUserEntityByEmail(companyInfoDto.getAdminEmail()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class,
                () -> companyService.createCompanyAndEmployee(companyInfoDto));

        String expectedMessage = "Can not found user by email";

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    @Transactional
    void updateCompanyDescription() {
        Company company = Company.builder()
                .id(1L)
                .name("AW")
                .adminId(11L)
                .description("com")
                .employees(new HashSet<>())
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        companyRepository.save(company);
        assertTrue(companyRepository.findById(1L).isPresent());
      /*  if (companyRepository.findById(1L).isPresent()) {
            System.out.println(companyRepository.findById(1L).get().getDescription());
        }

        CompanyInfoDto companyInfoDto = CompanyInfoDto.builder()
                .name("AWAW")
                .description("hahahaha")
                .build();
        companyService.updateCompanyDescription(companyInfoDto, 1L);*/
    }
}
