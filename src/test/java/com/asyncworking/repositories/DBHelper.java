package com.asyncworking.repositories;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class DBHelper {
	@Autowired
	protected CompanyRepository companyRepository;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected EmployeeRepository employeeRepository;

	protected void clearDb() {
		employeeRepository.deleteAll();
		employeeRepository.flush();

		companyRepository.deleteAll();
		companyRepository.flush();

		userRepository.deleteAll();
		userRepository.flush();
	}
}
