package com.asyncworking.repositories;

import com.asyncworking.constants.Status;
import com.asyncworking.models.*;
import io.awspring.cloud.messaging.listener.SimpleMessageListenerContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class EmployeeRepositoryTest extends DBHelper {
    private UserEntity mockUserEntity;
    private Company mockCompany;
    private Employee mockEmployee;

    @MockBean
    private SimpleMessageListenerContainer simpleMessageListenerContainer;

    @BeforeEach
    public void setUp() {
        clearDb();
        mockUserEntity = UserEntity.builder()
                .name("Lengary")
                .email("a@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.UNVERIFIED)
                .password("fff")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        mockCompany = Company.builder()
                .name("AW")
                .adminId(1L)
                .employees(new HashSet<>())
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        mockEmployee = Employee.builder()
                .id(new EmployeeId(mockUserEntity.getId(), mockCompany.getId()))
                .company(mockCompany)
                .userEntity(mockUserEntity)
                .title("kkk")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        userRepository.save(mockUserEntity);
        companyRepository.save(mockCompany);
        employeeRepository.save(mockEmployee);
    }

    @Test
    public void shouldAddEmployeeIntoDBSuccessfullyGivenProperEmployee() {
        List<Employee> employeeList = employeeRepository.findAll();
        assertNotNull(employeeList);
    }

    @Test
    public void shouldFindAllEmployeeGivenCompanyIdProvided() {

    }

    @Test
    public void shouldReturnCompanyIdSetGivenUserID() {
        Set<Long> companyIdSet = employeeRepository.findCompanyIdByUserId(mockUserEntity.getId());
        assertEquals(companyIdSet, Set.of(mockCompany.getId()));
    }
}
