package com.asyncworking.controllers;

import com.asyncworking.dtos.EmployeeInfoDto;
import com.asyncworking.models.Employee;
import com.asyncworking.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/create_company")
    public ResponseEntity<EmployeeInfoDto> createCompany (EmployeeInfoDto employeeInfoDto){

           EmployeeInfoDto returnedDto = employeeService.createCompany(employeeInfoDto);
           return ResponseEntity.ok(returnedDto);
    }

}
