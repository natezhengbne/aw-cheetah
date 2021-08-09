package com.asyncworking.jwt;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JwtUsernameAndPasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        AuthenticationRequest authenticationRequest = new ObjectMapper().readValue(request.getInputStream(), AuthenticationRequest.class);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()
        );

        return authenticationManager.authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        String email = authResult.getName();

        String jwtToken = jwtService.creatJwtToken(email);

        UserEntity user = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with email: " + email));

        UserInfoDto userInfoDto = UserInfoDto.builder()
                .id(user.getId())
                .email(email)
                .name(user.getName())
                .accessToken(jwtToken)
                .build();

        String userInfoJson = new Gson().toJson(userInfoDto);

        setResponseBody(response, userInfoJson);
    }

    @Override
    @SneakyThrows
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("Wrong password or user email");

    }

    @SneakyThrows
    private void setResponseBody(HttpServletResponse response, String json) {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(json);
        out.flush();
        out.close();
    }
}
