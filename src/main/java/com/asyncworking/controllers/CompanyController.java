package com.asyncworking.controllers;

import com.asyncworking.dtos.CompanyColleagueDto;
import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.services.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
@Validated
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/company")
    public ResponseEntity<?> companyCreate(@Valid @RequestBody CompanyModificationDto companyModificationDto) {
        companyService.createCompanyAndEmployee(companyModificationDto);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/companyinfo")
    public ResponseEntity companyInfoDisplay(@RequestParam(value = "email", required = true) String email) {
        log.info("email: {}", email);
        CompanyColleagueDto companyInfoDto = companyService.getCompanyInfoDto(email);
        return ResponseEntity.ok(companyInfoDto);
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
}
