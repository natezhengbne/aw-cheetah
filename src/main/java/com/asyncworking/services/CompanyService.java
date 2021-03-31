package com.asyncworking.services;

import com.asyncworking.dtos.CompanyColleagueDto;
import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.dtos.CompanyInfoGetDto;
import com.asyncworking.dtos.CompanyInfoPostDto;
import com.asyncworking.dtos.EmployeeGetDto;
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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

	private final UserRepository userRepository;

	private final CompanyRepository companyRepository;

	private final EmployeeRepository employeeRepository;

	//<<<<<<< HEAD
//    private final Mapper mapper;
//
//    @Transactional
//    public void createCompanyAndEmployee(CompanyModificationDto companyModificationDto) {
//
//        UserEntity selectedUserEntity = fetchUserEntityByEmail(companyModificationDto.getAdminEmail());
//        log.info("selectedUser's email" + selectedUserEntity.getEmail());
//        Company newCompany = createCompany(companyModificationDto.getName(), selectedUserEntity.getId());
//=======
	private final CompanyMapper companyMapper;

	private final UserMapper userMapper;

	private final EmployeeMapper employeeMapper;

	@Transactional
	public void createCompanyAndEmployee(CompanyInfoPostDto companyInfoPostDto) {

		UserEntity selectedUserEntity = fetchUserEntityByEmail(companyInfoPostDto.getAdminEmail());
		log.info("selectedUser's email" + selectedUserEntity.getEmail());
		Company newCompany = createCompany(companyInfoPostDto.getName(), selectedUserEntity.getId());

		companyRepository.save(newCompany);

		Employee newEmployee = createEmployee
				(new EmployeeId(selectedUserEntity.getId(), newCompany.getId()),
						selectedUserEntity,
						newCompany);
		if (companyInfoPostDto.getUserTitle() != null) {
			newEmployee.setTitle(companyInfoPostDto.getUserTitle());
			if (companyInfoPostDto.getUserTitle() != null) {
				newEmployee.setTitle(companyInfoPostDto.getUserTitle());
			}
			employeeRepository.save(newEmployee);
		}
	}

	public CompanyColleagueDto getCompanyInfoDto(String email) {
		if (companyRepository.findCompanyInfoByEmail(email) == null ||
				companyRepository.findCompanyInfoByEmail(email).isEmpty()) {
			throw new CompanyNotFoundException("company not found");
		} else {
			ICompanyInfo companyInfo = companyRepository.findCompanyInfoByEmail(email).get(0);
			List<String> colleague = userRepository.findNameById(companyInfo.getId());

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
		return userMapper.mapEntityToCompanyProfileDto(company);
	}

	private Company fetchCompanyById(Long companyId) {
		return companyRepository
				.findById(companyId)
				.orElseThrow(() -> new CompanyNotFoundException("Can not found company with Id:" + companyId));
	}

	@Transactional
	public void updateCompany(CompanyModificationDto companyModificationDto) {
		Company company = userMapper.mapInfoDtoToEntity(companyModificationDto);

		int res = companyRepository.updateCompanyProfileById(company.getId(),
				company.getName(),
				company.getDescription(),
				new Date());

		if (res == 0) {
			throw new CompanyNotFoundException("Can not found company with Id:" + company.getId());
		}
	}

	public CompanyInfoGetDto findCompanyById(Long id) {
		Optional<Company> foundCompany = companyRepository.findById(id);

		if (foundCompany.isEmpty()) {
			throw new CompanyNotFoundException("Can not found company by id:" + id);
		}

		return companyMapper.mapEntityToDto(foundCompany.get());
	}

	public List<EmployeeGetDto> findAllEmployeeByCompanyId(Long id) {
		Optional<List<IEmployeeInfo>> employees = userRepository.findAllEmployeeByCompanyId(id);

		if (employees.isEmpty()) {
			throw new EmployeeNotFoundException("Can not found employee by company id:" + id);
		}

		return employees.get()
				.stream()
				.map(employeeInfo -> {
					return employeeMapper.mapEntityToDto(employeeInfo);
				})
				.collect(Collectors.toList());
	}
}
