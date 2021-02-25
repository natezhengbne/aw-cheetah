package com.asyncworking.controllers;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
@AutoConfigureMockMvc
class UserControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void loginTest() throws Exception {
		Authentication mocked = Mockito.mock(Authentication.class);
		when(mocked.isAuthenticated()).thenReturn(true);
		when(userService.login(anyString(), anyString())).thenReturn(mocked);

		String inputJson = "{\"email\": \"lengary@asyncworking.com\", \"password\":\"len123\"}";
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.post("/login")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(inputJson)).andReturn();

		assertEquals(200, mvcResult.getResponse().getStatus());
	}

	@Test
	public void testCreateUser() throws Exception {

		UserInfoDto userPostInfoDto = UserInfoDto.builder()
				.name("aaa")
				.email("aaa@qq.com")
				.password("aaaaaaaa1")
				.build();
		UserInfoDto userGetInfoDto = UserInfoDto.builder()
				.name("aaa")
				.email("aaa@qq.com")
				.build();

		BDDMockito.given(userService.createUser(userPostInfoDto)).willReturn(userGetInfoDto);
		mockMvc.perform(post("/signup")
				.content(objectMapper.writeValueAsString(userPostInfoDto))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").exists())
				.andExpect(jsonPath("$.name").value("aaa"))
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.email").value("aaa@qq.com"));
	}

	@Test
	public void shouldValidEmailExist () throws Exception {
		UserInfoDto userFE = UserInfoDto.builder()
				.email("a@gmail.com")
				.build();

		when(userService.isEmailExist(userFE.getEmail())).thenReturn(true);

		String inputJson = "{\"email\": \"a@gmail.com\"}";
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.get("/signup")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(inputJson)).andReturn();

		assertEquals(400, mvcResult.getResponse().getStatus());
	}
}
