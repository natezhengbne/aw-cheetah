package com.asyncworking.controllers;

import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.services.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
@Validated
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/company")
    public ResponseEntity<?> companyCreate(@Valid @RequestBody CompanyModificationDto companyModificationDto){
        companyService.createCompanyAndEmployee(companyModificationDto);
        return ResponseEntity.ok("success");
    }
}
