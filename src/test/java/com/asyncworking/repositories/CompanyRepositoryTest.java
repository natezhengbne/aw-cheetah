package com.asyncworking.repositories;

import com.asyncworking.config.TestConfig;
import com.asyncworking.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@Import(TestConfig.class)
public class CompanyRepositoryTest extends DBHelper {

    Company mockCompany;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void insertMockEmp() {
        clearDb();
        when(passwordEncoder.encode("len123")).thenReturn("testpass");
    }

    @Test
    public void shouldAddCompanyIntoDBSuccessfullyGivenProperCompany() {
        Company mockCompany = Company.builder()
                .name("AW")
                .adminId(1L)
                .employees(new HashSet<>())
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        Company returnedCompany = companyRepository.save(mockCompany);
        assertEquals("AW", returnedCompany.getName());
    }

    @Test
    public void shouldReturn1BecauseOfSuccessfulModification() {
        saveMockData();
        int count = companyRepository.updateCompanyProfileById(mockCompany.getId(),
                "Async Working",
                "Startup company",
                OffsetDateTime.now(UTC));

        assertEquals(1, count);
    }

    @Test
    public void shouldGetCompanyInfoSuccessfullyGivenEmail() {
        String email = "a@asyncworking.com";
        saveMockData();
        List<ICompanyInfo> returnedCompany = companyRepository.findCompanyInfoByEmail(email);
        assertEquals("Lengary", returnedCompany.get(0).getName().trim());
    }

    @Test
    public void shouldGetEmployeeListSuccessfullyByGivenCompanyId() {
        saveMockData();
        List<String> employeeList = companyRepository.findNameById(mockCompany.getId());
        List<String> mockList = new ArrayList<>();
        mockList.add("Lengary");
        assertEquals(mockList, employeeList);
    }

    private void saveMockData() {
        UserEntity mockUser = UserEntity.builder()
            .name("Lengary")
            .email("a@asyncworking.com")
            .title("Frontend Developer")
            .status(Status.ACTIVATED)
            .password(passwordEncoder.encode("len123"))
            .createdTime(OffsetDateTime.now(UTC))
            .updatedTime(OffsetDateTime.now(UTC))
            .build();
        userRepository.save(mockUser);

         mockCompany = Company.builder()
            .name("Lengary")
            .description("description")
            .website("www.website.com")
            .adminId(mockUser.getId())
            .contactNumber("123345")
            .contactEmail("email@gmail.com")
            .industry("industry")
            .createdTime(OffsetDateTime.now(UTC))
            .updatedTime(OffsetDateTime.now(UTC))
            .build();
        companyRepository.save(mockCompany);

        EmployeeId mockEmployeeId = EmployeeId.builder()
            .userId(mockCompany.getId())
            .companyId(mockCompany.getId())
            .build();

        Employee mockEmployee = Employee.builder()
            .title("dev")
            .company(mockCompany)
            .userEntity(mockUser)
            .id(mockEmployeeId)
            .createdTime(OffsetDateTime.now(UTC))
            .updatedTime(OffsetDateTime.now(UTC))
            .build();

        employeeRepository.save(mockEmployee);
    }
}
