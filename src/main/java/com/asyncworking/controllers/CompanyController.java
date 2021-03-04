package com.asyncworking.controllers;

import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.exceptions.NoCompanyWithSuchUserException;
import com.asyncworking.services.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    @GetMapping
    public ResponseEntity<List<CompanyInfoDto>> getCompanies(@RequestParam("email") String email) {

        try {
            return ResponseEntity.ok(companyService.fetchCompaniesWithGivenUser(email));
        } catch (NoCompanyWithSuchUserException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
