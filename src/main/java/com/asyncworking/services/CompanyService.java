package com.asyncworking.services;

import com.asyncworking.dtos.CompanyColleagueDto;
import com.asyncworking.dtos.CompanyNameDescriptionColleagueDto;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

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
            if (companyModificationDto.getUserTitle() != null) {
                newEmployee.setTitle(companyModificationDto.getUserTitle());
            }
            employeeRepository.save(newEmployee);
        }

        if (companyModificationDto.getUserTitle() != null) {
            newEmployee.setTitle(companyModificationDto.getUserTitle());
        if (companyInfoDto.getUserTitle() != null) {
            newEmployee.setTitle(companyInfoDto.getUserTitle());
        }
        employeeRepository.save(newEmployee);
    }

    public CompanyColleagueDto getCompanyInfoDto(String email) {
        if (companyRepository.findCompanyInfoByEmail(email) == null ||
                companyRepository.findCompanyInfoByEmail(email).isEmpty()) {
            throw new CompanyNotFoundException("company not found");
        } else {
            ICompanyInfo companyInfo = companyRepository.findCompanyInfoByEmail(email).get(0);
            List<String> colleague = userRepository.findNameById(companyInfo.getCompanyId());

            return mapCompanyToCompanyDto(companyInfo, colleague);
        }
    }

    private CompanyColleagueDto mapCompanyToCompanyDto(ICompanyInfo companyInfo, List<String> colleague) {
        return CompanyColleagueDto.builder()
                .id(companyInfo.getCompanyId())
                .name(companyInfo.getName())
                .description(companyInfo.getDescription())
                .colleague(colleague)
                .build();
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
    public void updateCompany(CompanyModificationDto companyModificationDto) {
        Company company = mapper.mapInfoDtoToEntity(companyModificationDto);
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
