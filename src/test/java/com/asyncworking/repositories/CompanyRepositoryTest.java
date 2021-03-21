package com.asyncworking.repositories;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.models.Company;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

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
    

}
