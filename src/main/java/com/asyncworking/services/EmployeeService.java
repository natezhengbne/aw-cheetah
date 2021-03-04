package com.asyncworking.services;

import com.asyncworking.dtos.EmployeeInfoDto;
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
public class EmployeeService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;



    @Transactional
    public EmployeeInfoDto createCompany(EmployeeInfoDto employeeInfoDto){

        UserEntity selectedUser = fetchUserByEmployeeInfoDto(employeeInfoDto);
        System.out.println(selectedUser.getEmail());
        Company newCompany = mapDtoToEntity(employeeInfoDto, selectedUser.getId());

        Company createdCompany = companyRepository.save(newCompany);

        Employee employee = Employee.builder()
                .id(new EmployeeId(selectedUser.getId(),createdCompany.getId()))
                .company(createdCompany)
                .userEntity(selectedUser)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        if(employeeInfoDto.getUserTitle() != null){
            employee.setTitle(employeeInfoDto.getUserTitle());
        }
        employeeRepository.saveAndFlush(employee);

        return mapEntityToDto(employee);
    }

    private UserEntity fetchUserByEmployeeInfoDto(EmployeeInfoDto employeeInfoDto){
        return  userRepository.findByEmail(employeeInfoDto.getUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("No such user!"));
    }

    private Company mapDtoToEntity(EmployeeInfoDto employeeInfoDto, Long userId){
        return Company.builder()
                .name(employeeInfoDto.getCompanyName())
                .adminId(userId)
                .employees(new HashSet<>())
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private EmployeeInfoDto mapEntityToDto(Employee employee){

        return  EmployeeInfoDto.builder()
                .companyId(employee.getCompany().getId())
                .companyName(employee.getCompany().getName())
                .userEmail(employee.getUserEntity().getEmail())
                .build();
    }
}
