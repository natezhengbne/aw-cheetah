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


        //UserEntity savedUser = userRepository.saveAndFlush(userEntity);

        Company company = Company.builder()
                .id(1L)
                .adminId(1L)
                .name("Async Working")
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .employees(new HashSet<>())
                .build();


        userEntity.getEmployees().add(
                Employee.builder()
                .id(new EmployeeId(userEntity.getId(), company.getId()))
                .userEntity(userEntity)
                .company(company)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build()
        );
        userRepository.saveAndFlush(userEntity);

        Optional<Company> selectedCompany = companyRepository.findById(1L);

        Assertions.assertFalse(selectedCompany.isEmpty());
        //Assertions.assertEquals("Async Working", companiesWithGivenEmail.get(0).getName());


    }

}
