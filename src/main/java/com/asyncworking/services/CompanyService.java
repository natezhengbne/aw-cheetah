package com.asyncworking.services;

import com.asyncworking.dtos.*;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.EmployeeNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.CompanyMapper;
import com.asyncworking.utility.mapper.EmployeeMapper;
import com.asyncworking.utility.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final EmployeeRepository employeeRepository;

    private final CompanyMapper companyMapper;

    private final UserMapper userMapper;

    private final EmployeeMapper employeeMapper;

    private final RoleService roleService;

    @Transactional
    public Long createCompanyAndEmployee(CompanyModificationDto companyModificationDto) {

        UserEntity selectedUserEntity = fetchUserEntityByEmail(companyModificationDto.getAdminEmail());
        log.info("selectedUser's email" + selectedUserEntity.getEmail());
        Company newCompany = createCompany(companyModificationDto.getName(), selectedUserEntity.getId());

        companyRepository.save(newCompany);

        roleService.assignRole(selectedUserEntity, RoleNames.COMPANY_MANAGER, newCompany.getId());

        Employee newEmployee = createEmployee
                (new EmployeeId(selectedUserEntity.getId(), newCompany.getId()),
                        selectedUserEntity,
                        newCompany);
        if (companyModificationDto.getUserTitle() != null) {
            newEmployee.setTitle(companyModificationDto.getUserTitle());
            employeeRepository.save(newEmployee);
        }
        return newCompany.getId();
    }

    public CompanyColleagueDto getCompanyInfoDto(String email) {
        if (companyRepository.findCompanyInfoByEmail(email) == null ||
                companyRepository.findCompanyInfoByEmail(email).isEmpty()) {
            throw new CompanyNotFoundException("company not found");
        } else {
            ICompanyInfo companyInfo = companyRepository.findCompanyInfoByEmail(email).get(0);
            List<String> colleague = companyRepository.findNameById(companyInfo.getId());

            log.info("company ID: {}", companyInfo.getId());
            return mapCompanyToCompanyDto(companyInfo, colleague);
        }
    }

    private CompanyColleagueDto mapCompanyToCompanyDto(ICompanyInfo companyInfo, List<String> colleague) {
        return CompanyColleagueDto.builder()
                .companyId(companyInfo.getId())
                .name(companyInfo.getName())
                .description(companyInfo.getDescription())
                .colleague(colleague)
                .build();
    }

    private UserEntity fetchUserEntityByEmail(String email) {
        return userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Can not find user by email:" + email));
    }

    private Company createCompany(String company, Long userId) {
        return Company.builder()
                .name(company)
                .adminId(userId)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .employees(new HashSet<>())
                .build();
    }

    private Employee createEmployee(EmployeeId employeeId, UserEntity userEntity, Company company) {
        return Employee.builder()
                .id(employeeId)
                .company(company)
                .userEntity(userEntity)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
    }

    public CompanyModificationDto fetchCompanyProfileById(Long companyId) {
        Company company = fetchCompanyById(companyId);
        return userMapper.mapEntityToCompanyProfileDto(company);
    }

    public Company fetchCompanyById(Long companyId) {
        return companyRepository
                .findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Can not find company with Id:" + companyId));
    }

    @Transactional
    public void updateCompany(CompanyModificationDto companyModificationDto) {
        Company company = userMapper.mapInfoDtoToEntity(companyModificationDto);

        int res = companyRepository.updateCompanyProfileById(company.getId(),
                company.getName(),
                company.getDescription(),
                OffsetDateTime.now(UTC));

        if (res == 0) {
            throw new CompanyNotFoundException("Can not find company with Id:" + company.getId());
        }
    }

    public CompanyInfoDto findCompanyById(Long id) {
        Optional<Company> foundCompany = companyRepository.findById(id);
        if (foundCompany.isEmpty()) {
            throw new CompanyNotFoundException("Can not find company by id:" + id);
        }
        return companyMapper.mapEntityToDto(foundCompany.get());
    }

    public List<EmployeeGetDto> findAllEmployeeByCompanyId(Long id) {
        log.info("company ID: {}", id);
        List<IEmployeeInfo> employees = userRepository.findAllEmployeeByCompanyId(id);
        if (employees.isEmpty()) {
            throw new EmployeeNotFoundException("Can not find employee by company id:" + id);
        }
        List<EmployeeGetDto> employeeGetDtoList = new ArrayList<>();
        for (IEmployeeInfo iEmployeeInfo : employees) {
            employeeGetDtoList.add(employeeMapper.mapEntityToDto(iEmployeeInfo));
        }
        return employeeGetDtoList;
    }

    public List<AvailableEmployeesGetDto> findAvailableEmployees(Long companyId, Long projectId) {
        log.info("Project ID: {}", projectId);
        log.info("Company ID: {}", companyId);
        //how to verify the pinvitations/registerrojectId and companyId if they are both invalid the result is always empty arrays
        return userRepository.findAvailableEmployeesByCompanyAndProjectId(companyId, projectId).stream()
                .map(employeeMapper::mapAvailableEmployeesEntityToDto)
                .collect(Collectors.toList());
    }
}
