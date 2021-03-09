package com.asyncworking.controllers;

import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.CompanyService;
import com.asyncworking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/company_create")
    public ResponseEntity<?> companyCreate(@RequestBody CompanyInfoDto companyInfoDto){
        try {
            companyService.createCompanyAndEmployee(companyInfoDto);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
