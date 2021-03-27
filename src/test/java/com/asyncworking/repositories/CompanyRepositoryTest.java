package com.asyncworking.repositories;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CompanyRepositoryTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void insertMockEmp() throws InterruptedException {
        companyRepository.deleteAll();
        employeeRepository.deleteAll();
        userRepository.deleteAll();
        when(passwordEncoder.encode("len123")).thenReturn("testpass");

        Company mockCompany = Company.builder()
                .id(1L)
                .name("Lengary")
                .description("description")
                .website("www.website.com")
                .adminId(1L)
                .contactNumber("123345")
                .contactEmail("email@gmail.com")
                .industry("industry")
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();

        UserEntity mockUser = UserEntity.builder()
                .id(1L)
                .name("Lengary")
                .email("a@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.ACTIVATED)
                .password(passwordEncoder.encode("len123"))
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();

        EmployeeId mockEmployeeId = EmployeeId.builder()
                .userId(1L)
                .companyId(1L)
                .build();

        Employee mockEmployee = Employee.builder()
                .title("dev")
                .company(mockCompany)
                .userEntity(mockUser)
                .id(mockEmployeeId)
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();

        companyRepository.save(mockCompany);
        userRepository.save(mockUser);
        employeeRepository.save(mockEmployee);

    }

    @AfterEach
    void tearDown() {
        companyRepository.deleteAll();
    }

    @Test
    public void shouldAddCompanyIntoDBSuccessfullyGivenProperCompany() {

        Company mockCompany = Company.builder()
                .id(1L)
                .name("AW")
                .adminId(1L)
                .employees(new HashSet<>())
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();
        Company returnedCompany = companyRepository.save(mockCompany);
        assertEquals("AW", returnedCompany.getName());

    }

    @Transactional
    @Test
    public void shouldReturn1BecauseOfSuccessfulModification() {

        Company mockCompany = Company.builder()
                .id(1L)
                .name("AW")
                .adminId(1L)
                .employees(new HashSet<>())
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();
        companyRepository.save(mockCompany);

        Company demoCompany = Company.builder()
                .id(2L)
                .name("AW")
                .adminId(1L)
                .employees(new HashSet<>())
                .build();
        companyRepository.save(demoCompany);

        int count = companyRepository.updateCompanyProfileById("Async Working", "Startup company", new Date(), 1L);

        assertEquals(1, count);

    }



    @Test
    public void shouldGetCompanyInfoSuccessfullyGivenEmail() {
        String email = "a@asyncworking.com";
        List<ICompanyInfo> returnedCompany = companyRepository.findCompanyInfoByEmail(email);
        assertEquals("Lengary", returnedCompany.get(0).getName().trim());
    }
}
