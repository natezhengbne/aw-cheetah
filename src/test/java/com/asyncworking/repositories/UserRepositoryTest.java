//package com.asyncworking.repositories;
//
//import com.asyncworking.models.Status;
//import com.asyncworking.models.UserEntity;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.time.OffsetDateTime;
//import java.time.ZoneOffset;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//public class UserRepositoryTest {
//
//	@MockBean
//	private PasswordEncoder passwordEncoder;
//
//	@Autowired
//	private UserRepository userRepository;
//
//	@BeforeEach
//	public void insertMockEmp() {
//		userRepository.deleteAll();
//		when(passwordEncoder.encode("len123")).thenReturn("testpass");
//
//		UserEntity mockUser = UserEntity.builder()
//				.id(1)
//				.name("Lengary")
//				.email("a@asyncworking.com")
//				.title("Frontend Developer")
//				.status(Status.UNVERIFIED)
//				.password(passwordEncoder.encode("len123"))
//				.createdTime(OffsetDateTime.now(ZoneOffset.UTC))
//				.updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
//				.build();
//
//		userRepository.saveAndFlush(mockUser);
//	}
//
//	@Test
//	public void shouldFindUserByName() {
//		Optional<UserEntity> userEntity = userRepository.findByEmail("a@asyncworking.com");
//		assertEquals("testpass", userEntity.get().getPassword());
//	}
//}