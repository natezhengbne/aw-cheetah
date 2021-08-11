package com.asyncworking.auth;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@Component
@ControllerAdvice
public class AuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException authException) throws IOException {
        httpServletResponse.setStatus(NOT_ACCEPTABLE.value());
        setResponseBody(httpServletResponse,"Authentication Failed Due To Invalid Token");
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AccessDeniedException accessDeniedException) throws IOException {
        httpServletResponse.setStatus(FORBIDDEN.value());
        setResponseBody(httpServletResponse, "Access Denied Due To Non-authorization");
    }

    private void setResponseBody(HttpServletResponse response, String message) throws IOException {
        PrintWriter out = response.getWriter();
        out.print(message);
        out.flush();
        out.close();
    }
}

