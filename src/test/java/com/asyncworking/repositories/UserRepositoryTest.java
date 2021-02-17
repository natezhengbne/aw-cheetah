package com.asyncworking.repositories;

import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserRepositoryTest {

	@Autowired
	private  UserRepository repository;

	@Test
	@Transactional
	public void save() {
		UserEntity user = UserEntity.builder()
				.name("username")
				.email("email")
				.title("title")
				.password("password")
				.status(Status.Unverified)
				.createdTime(OffsetDateTime.now())
				.updatedTime(OffsetDateTime.now())
				.build();

		UserEntity saved = repository.save(user);
		assertEquals("email", saved.getEmail());
		assertThat(saved.getId() > 0);
	}
}
