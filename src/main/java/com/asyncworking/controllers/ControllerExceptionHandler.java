package com.asyncworking.controllers;

import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.EmployeeNotFoundException;
import com.asyncworking.exceptions.ErrorDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(value = {UserNotFoundException.class})
	public ResponseEntity<ErrorDto> handleUserNotFoundException(UserNotFoundException e) {
		log.info("User is not found.", e);

		List<String> details = new ArrayList<>();
		details.add(e.getLocalizedMessage());
		ErrorDto error = new ErrorDto("User not found", details);
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = {CompanyNotFoundException.class})
	public ResponseEntity<ErrorDto> handleCompanyNotFoundException(CompanyNotFoundException e) {
		log.info("Company is not found.", e);

        List<String> details = new ArrayList<>();
        details.add(e.getLocalizedMessage());
        ErrorDto error = new ErrorDto("Company not found", details);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ProjectNotFoundException.class})
    public ResponseEntity<ErrorDto> handleProjectNotFoundException(ProjectNotFoundException e) {
        log.info("Project is not found.", e);

        List<String> details = new ArrayList<>();
        details.add(e.getLocalizedMessage());
        ErrorDto error = new ErrorDto("Project not found", details);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = {EmployeeNotFoundException.class})
	public ResponseEntity<ErrorDto> handleEmployeeNotFoundException(EmployeeNotFoundException e) {
		log.info("Employee is not found.", e);

		List<String> details = new ArrayList<>();
		details.add(e.getLocalizedMessage());
		ErrorDto error = new ErrorDto("Employee not found", details);
		return new ResponseEntity(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<ErrorDto> handleArgumentNotValid(MethodArgumentNotValidException e) {
		List<String> details = new ArrayList<>();
		for (ObjectError error : e.getBindingResult().getAllErrors()) {
			details.add(error.getDefaultMessage());
		}
		ErrorDto error = new ErrorDto("Validation Failed", details);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = {MissingServletRequestParameterException.class})
	public ResponseEntity<ErrorDto> handleMissingParams(MissingServletRequestParameterException e) {
		List<String> details = new ArrayList<>();
		details.add(e.getLocalizedMessage());
		ErrorDto error = new ErrorDto("Missing Params", details);
		return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = {AuthenticationException.class})
	public ResponseEntity<ErrorDto> handleBadCredential(AuthenticationException e) {
		List<String> details = new ArrayList<>();
		details.add(e.getLocalizedMessage());
		ErrorDto errorDto = new ErrorDto("Authentication Failed", details);
		return new ResponseEntity<ErrorDto>(errorDto, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDto> handleAllExceptions(Exception e) {
		log.error("There is Exception occurred", e);

		List<String> details = new ArrayList<>();
		details.add(e.getLocalizedMessage());
		ErrorDto error = new ErrorDto("Server Error", details);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
