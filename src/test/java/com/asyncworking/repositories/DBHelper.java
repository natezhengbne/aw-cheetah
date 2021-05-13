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
	protected TodoBoardRepository todoBoardRepository;

	@Autowired
	protected TodoListRepository todoListRepository;

	@Autowired
	protected MessageRepository messageRepository;

	protected void clearDb() {
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

		todoBoardRepository.deleteAll();
		todoBoardRepository.flush();

		todoListRepository.deleteAll();
		todoListRepository.flush();

		messageRepository.deleteAll();
		messageRepository.flush();

	}
}
