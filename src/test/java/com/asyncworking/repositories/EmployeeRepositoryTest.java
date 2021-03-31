package com.asyncworking.repositories;

import com.asyncworking.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class EmployeeRepositoryTest extends DBHelper {

    @BeforeEach
    void setUp() {
        clearDb();
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

    @Test
    public void shouldFindAllEmployeeGivenCompanyIdProvided() {

    }
}
