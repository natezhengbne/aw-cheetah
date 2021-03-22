package com.asyncworking.services;

import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.Company;
import com.asyncworking.models.Employee;
import com.asyncworking.models.EmployeeId;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final EmployeeRepository employeeRepository;

    private final Mapper mapper;

    @Transactional
    public void createCompanyAndEmployee(CompanyModificationDto companyModificationDto) {

        UserEntity selectedUserEntity = fetchUserEntityByEmail(companyModificationDto.getAdminEmail());
        log.info("selectedUser's email" + selectedUserEntity.getEmail());
        Company newCompany = createCompany(companyModificationDto.getName(), selectedUserEntity.getId());

        companyRepository.save(newCompany);

        Employee newEmployee = createEmployee
                (new EmployeeId(selectedUserEntity.getId(), newCompany.getId()),
                        selectedUserEntity,
                        newCompany);
        if (companyModificationDto.getUserTitle() != null) {
            newEmployee.setTitle(companyModificationDto.getUserTitle());
        }
        employeeRepository.save(newEmployee);
    }

    private UserEntity fetchUserEntityByEmail(String email) {
        return userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Can not found user by email:" + email));
    }

    private Company createCompany(String company, Long userId) {
        return Company.builder()
                .name(company)
                .adminId(userId)
                .employees(new HashSet<>())
                .build();
    }

    private Employee createEmployee(EmployeeId employeeId, UserEntity userEntity, Company company) {
        return Employee.builder()
                .id(employeeId)
                .company(company)
                .userEntity(userEntity)
                .build();
    }

    public CompanyModificationDto fetchCompanyProfileById(Long companyId) {
        Company company = fetchCompanyById(companyId);
        return mapper.mapEntityToCompanyProfileDto(company);
    }

    private Company fetchCompanyById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Can not found company with Id:" + companyId));
    }

    @Transactional
    public void updateCompany(CompanyModificationDto CompanyModificationDto) {
        Company company = mapper.mapInfoDtoToEntity(CompanyModificationDto);
        int res = companyRepository.updateCompanyProfileById(
                company.getName(),
                company.getDescription(),
                new Date(),
                company.getId());
        if (res == 0) {
            throw new CompanyNotFoundException("Can not found company with Id:" + company.getId());
        }
    }
}
