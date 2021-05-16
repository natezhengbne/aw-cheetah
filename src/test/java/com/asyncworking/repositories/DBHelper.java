package com.asyncworking.repositories;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class DBHelper {
	@Autowired
	protected CompanyRepository companyRepository;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected EmployeeRepository employeeRepository;

	@Autowired
	protected ProjectUserRepository projectUserRepository;

	@Autowired
	protected ProjectRepository projectRepository;

	@Autowired
	protected TodoListRepository todoListRepository;

	@Autowired
	protected TodoItemRepository todoItemRepository;

	protected void clearDb() {
		todoItemRepository.deleteAll();
		todoItemRepository.flush();

		todoListRepository.deleteAll();
		todoListRepository.flush();

		employeeRepository.deleteAll();
		employeeRepository.flush();

		companyRepository.deleteAll();
		companyRepository.flush();

		userRepository.deleteAll();
		userRepository.flush();

		projectUserRepository.deleteAll();
		projectUserRepository.flush();

		projectRepository.deleteAll();
		projectRepository.flush();
	}
}
