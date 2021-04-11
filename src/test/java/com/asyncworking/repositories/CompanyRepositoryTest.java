package com.asyncworking.repositories;

import com.asyncworking.models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class CompanyRepositoryTest extends DBHelper {

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void insertMockEmp() {
        clearDb();
        when(passwordEncoder.encode("len123")).thenReturn("testpass");
    }

//    @AfterEach
//    public void clearDb() {
//        clearDb();
//    }

    @Test
    public void shouldAddCompanyIntoDBSuccessfullyGivenProperCompany() {
        Company mockCompany = Company.builder()
                .name("AW")
                .adminId(1L)
                .employees(new HashSet<>())
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();
        Company returnedCompany = companyRepository.save(mockCompany);
        assertEquals("AW", returnedCompany.getName());
    }

    @Test
    public void shouldReturn1BecauseOfSuccessfulModification() {
        saveMockData();
        UserEntity savedUser = userRepository.findByEmail("a@asyncworking.com").get();
        Company mockCompany = Company.builder()
                .name("AW")
                .adminId(savedUser.getId())
                .employees(new HashSet<>())
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();
        companyRepository.save(mockCompany);

        int count = companyRepository.updateCompanyProfileById(mockCompany.getId(), "Async Working", "Startup company", new Date());

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
    public void shouldGetEmployeeListSuccessfullyGivenEmail() {
        saveMockData();
        List<String> employeeList = companyRepository.findNameById(1L);
        List<String> mockList = new ArrayList<>();
        assertEquals(mockList, employeeList);
    }

    private void saveMockData() {
        UserEntity mockUser = UserEntity.builder()
            .name("Lengary")
            .email("a@asyncworking.com")
            .title("Frontend Developer")
            .status(Status.ACTIVATED)
            .password(passwordEncoder.encode("len123"))
            .createdTime(new Date())
            .updatedTime(new Date())
            .build();
        userRepository.save(mockUser);

        Company mockCompany = Company.builder()
            .name("Lengary")
            .description("description")
            .website("www.website.com")
            .adminId(mockUser.getId())
            .contactNumber("123345")
            .contactEmail("email@gmail.com")
            .industry("industry")
            .createdTime(new Date())
            .updatedTime(new Date())
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
            .createdTime(new Date())
            .updatedTime(new Date())
            .build();

        employeeRepository.save(mockEmployee);
    }
}
