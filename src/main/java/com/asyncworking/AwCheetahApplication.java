package com.asyncworking;

import com.asyncworking.models.*;
import com.asyncworking.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;

@SpringBootApplication
public class AwCheetahApplication {
	public static void main(String[] args) {
		SpringApplication.run(AwCheetahApplication.class, args);
	}

	/*@Bean
	CommandLineRunner commandLineRunner (
			UserRepository userRepository
	) {
		return args -> {
			UserEntity selectedUser = userRepository.findByEmail("a@asyncworking.com").get();
			Company company = Company.builder()
					.adminId(userEntity.getId())
					.name("Async Working")
					.employees(new HashSet<>())
					.build();

			userEntity.addEmployee(
					Employee.builder()
							.userEntity(userEntity)
							.company(company)
							.build()
			);

			userRepository.save(userEntity);


		};
	}*/
}
