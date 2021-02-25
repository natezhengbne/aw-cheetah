package com.asyncworking.repositories;

import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserRepositoryTest {

	@Autowired
	private  UserRepository userRepository;

	@Test
	@Transactional
	public void save() {
		UserEntity user = UserEntity.builder()
				.name("username")
				.email("email")
				.title("title")
				.password("password")
				.status(Status.UNVERIFIED)
				.createdTime(OffsetDateTime.now())
				.updatedTime(OffsetDateTime.now())
				.build();

		UserEntity saved = userRepository.save(user);
		assertEquals("email", saved.getEmail());
		assertThat(saved.getId() > 0);
	}

	@Test
	public void shouldAddUserEntityIntoDBSuccessfullyGivenProperUserEntity() {
		UserEntity userEntity = UserEntity.builder()
				.id(1L)
				.createdTime(OffsetDateTime.now())
				.email("skykk0128@gmail.com")
				.name("Steven")
				.password("password")
				.status(Status.UNVERIFIED)
				.title("Developer")
				.updatedTime(OffsetDateTime.now()).build();
		UserEntity returnedUserEntity = userRepository.save(userEntity);
		Assertions.assertEquals("Steven", returnedUserEntity.getName());
		Assertions.assertEquals("skykk0128@gmail.com", returnedUserEntity.getEmail());
	}

	@Test
	public void shouldFindUserByEmail() {
		UserEntity userEntity = UserEntity.builder()
				.id(1L)
				.createdTime(OffsetDateTime.now())
				.email("skykk0128@gmail.com")
				.name("Steven")
				.password("password")
				.status(Status.UNVERIFIED)
				.title("Developer")
				.updatedTime(OffsetDateTime.now()).build();
		Optional<UserEntity> returnedUserEntity = userRepository
				.findByEmail(userEntity.getEmail());
		Assertions.assertTrue(returnedUserEntity.isEmpty());
	}
}
