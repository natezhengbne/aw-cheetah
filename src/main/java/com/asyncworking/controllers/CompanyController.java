package com.asyncworking.controllers;

import com.asyncworking.dtos.*;
import com.asyncworking.services.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/company")
    public ResponseEntity companyCreate(@Valid @RequestBody CompanyModificationDto companyModificationDto) {
        return ResponseEntity.ok(companyService.createCompanyAndEmployee(companyModificationDto));
    }

    @GetMapping("/companyinfo")
    public ResponseEntity companyInfoDisplay(@RequestParam(value = "email") String email) {
        log.info("email: {}", email);
        CompanyColleagueDto companyColleagueDto = companyService.getCompanyInfoDto(email);
        return ResponseEntity.ok(companyColleagueDto);
    }

    @GetMapping("/company/profile")
    public ResponseEntity<CompanyModificationDto> prefillDescription(@RequestParam("companyId")
                                                                     @NotNull Long companyId) {
        return ResponseEntity.ok(companyService.fetchCompanyProfileById(companyId));
    }

    @PutMapping("/company/profile")
    public ResponseEntity<?> updateCompanyDescription(@Valid
                                                      @RequestBody CompanyModificationDto companyModificationDto) {
        companyService.updateCompany(companyModificationDto);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/company/{id}")
    public ResponseEntity getCompanyById(@PathVariable Long id) {
        log.info("company ID: {}", id);
        CompanyInfoDto companyInfoDto = companyService.findCompanyById(id);
        return ResponseEntity.ok(companyInfoDto);
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity getEmployeeByCompanyId(@PathVariable Long id) {
        log.info("company ID: {}", id);
        List<EmployeeGetDto> employees = companyService.findAllEmployeeByCompanyId(id);
        return ResponseEntity.ok(employees);
    }
}
