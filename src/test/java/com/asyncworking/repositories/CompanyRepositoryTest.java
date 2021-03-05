package com.asyncworking.repositories;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
public class CompanyRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    public void shouldReturnCompanyInfoGivenRelativeUserEmail() {

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("Lengary")
                .email("a@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.ACTIVATED)
                .password(passwordEncoder.encode("len123"))
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .employees(new HashSet<>())
                .build();

        UserEntity userEntity2 = UserEntity.builder()
                .id(2L)
                .name("Robert")
                .email("b@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.ACTIVATED)
                .password(passwordEncoder.encode("rob123"))
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .employees(new HashSet<>())
                .build();


        UserEntity savedUser = userRepository.saveAndFlush(userEntity);
        userRepository.saveAndFlush(userEntity2);

        Company company = Company.builder()
                .id(1L)
                .adminId(1L)
                .name("Async Working")
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .employees(new HashSet<>())
                .build();

        //Company savedCompany = companyRepository.save(company);

        Company savedCompany = companyRepository.saveAndFlush(company);

        Employee employee = Employee.builder()
                .id(new EmployeeId(savedUser.getId(), company.getId()))
                .userEntity(savedUser)
                .company(savedCompany)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        employeeRepository.saveAndFlush(employee);


        List<Company> selectedCompany = companyRepository.findAll();

        //Assertions.assertTrue(selectedCompany.get(0).getEmployees().contains(employee));
       // Assertions.assertEquals(1L, selectedCompany.));
        Assertions.assertFalse(selectedCompany.isEmpty());
        Assertions.assertTrue(!employeeRepository.findByUserId(1L).isEmpty());
        Assertions.assertTrue(!userRepository.findAllByEmail(savedUser.getEmail()).isEmpty());
        Assertions.assertTrue(userRepository.findAllByEmail("b@asyncworking.com").isEmpty());
        System.out.println(companyRepository.findCompaniesByUserId(savedUser.getId()));

    }

}
