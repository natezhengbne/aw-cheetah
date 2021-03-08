package com.asyncworking.services;

import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Company;
import com.asyncworking.models.Employee;
import com.asyncworking.models.EmployeeId;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final EmployeeRepository employeeRepository;

    @Transactional
    public void createCompanyAndEmployee(CompanyInfoDto companyInfoDto) {

        UserEntity selectedUserEntity = fetchUserEntityByEmail(companyInfoDto.getAdminEmail());
        log.info("selectedUser's email" + selectedUserEntity.getEmail());
        Company newCompany = createCompany(companyInfoDto.getName(), selectedUserEntity.getId());

        saveCompany(newCompany);

        Employee newEmployee = createEmployee
                (new EmployeeId(selectedUserEntity.getId(), newCompany.getId()),
                        selectedUserEntity,
                        newCompany);
        if (companyInfoDto.getUserTitle() != null){
            newEmployee.setTitle(companyInfoDto.getUserTitle());
        }
        saveEmployee(newEmployee);
    }

    private UserEntity fetchUserEntityByEmail(String email) {

        return userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No such user!"));
    }

    private Company createCompany(String company, Long userId){
        return Company.builder()
                .name(company)
                .adminId(userId)
                .employees(new HashSet<>())
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private void saveCompany(Company company) {
        try {
            companyRepository.save(company);
        } catch (Exception e) {
            log.error("Something wrong when saving to database " + e.getMessage(), e);
        }
    }

    private Employee createEmployee(EmployeeId employeeId, UserEntity userEntity, Company company) {
        return Employee.builder()
                .id(employeeId)
                .company(company)
                .userEntity(userEntity)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private void saveEmployee(Employee employee) {
        try {
            employeeRepository.save(employee);
        } catch (Exception e) {
            log.error("Something wrong when saving to database " + e.getMessage(), e);
        }
    }
}
