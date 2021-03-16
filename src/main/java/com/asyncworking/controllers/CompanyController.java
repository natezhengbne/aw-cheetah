package com.asyncworking.controllers;

import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.services.CompanyService;
import com.asyncworking.utility.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
@Validated
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/company")
    public ResponseEntity<?> companyCreate(@Valid @RequestBody CompanyInfoDto companyInfoDto){
        companyService.createCompanyAndEmployee(companyInfoDto);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/company/description")
    public ResponseEntity<String> prefillDescription(@Valid @RequestParam("companyId") Long companyId){
        return ResponseEntity.ok(companyService.fetchCompanyDescriptionById(companyId));
    }

    @PutMapping("/company/description")
    public ResponseEntity<?> updateCompanyDescription(@Valid @RequestBody CompanyInfoDto companyInfoDto){
        companyService.updateCompanyDescription(companyInfoDto);
        return ResponseEntity.ok("success");
    }
}
