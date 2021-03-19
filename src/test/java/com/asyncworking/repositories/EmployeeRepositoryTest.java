package com.asyncworking.repositories;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    public void shouldAddEmployeeIntoDBSuccessfullyGivenProperEmployee() {
        UserEntity mockUserEntity = UserEntity.builder()
                .id(1L)
                .name("Lengary")
                .email("a@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.UNVERIFIED)
                .password("fff")
                .build();
        Company mockCompany = Company.builder()
                .id(1L)
                .name("AW")
                .adminId(1L)
                .employees(new HashSet<>())
                .build();
        Employee mockEmployee = Employee.builder()
                .id(new EmployeeId(mockUserEntity.getId(), mockCompany.getId()))
                .company(mockCompany)
                .userEntity(mockUserEntity)
                .title("kkk")
                .build();
        employeeRepository.save(mockEmployee);
        List<Employee> employeeList = employeeRepository.findAll();
        assertNotNull(employeeList);
    }
}
