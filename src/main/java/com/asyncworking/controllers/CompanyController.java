package com.asyncworking.controllers;

import com.asyncworking.dtos.AvailableEmployeesGetDto;
import com.asyncworking.dtos.CompanyColleagueDto;
import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.dtos.CompanyInvitedAccountDto;
import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.dtos.EmployeeGetDto;
import com.asyncworking.dtos.todoitem.CardTodoItemDto;
import com.asyncworking.services.CompanyService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity createCompany(@Valid @RequestBody CompanyModificationDto companyModificationDto) {
        return ResponseEntity.ok(companyService.createCompanyAndEmployee(companyModificationDto));
    }

    @GetMapping("/company-info")
    public ResponseEntity getCompanyInfoColleagues(@RequestParam(value = "email") String email) {
        log.info("email: {}", email);
        CompanyColleagueDto companyColleagueDto = companyService.getCompanyInfoDto(email);
        return ResponseEntity.ok(companyColleagueDto);
    }

    @GetMapping("/{companyId}/profile")
    public ResponseEntity<CompanyModificationDto> fetchCompanyProfile(@PathVariable("companyId")
                                                                      @NotNull Long companyId) {
        return ResponseEntity.ok(companyService.fetchCompanyProfileById(companyId));
    }

    @PutMapping("/{companyId}/profile")
    @PreAuthorize("hasPermission(#companyId, 'Company Manager')")
    public ResponseEntity updateCompanyProfile(@PathVariable("companyId") Long companyId,
                                               @Valid @RequestBody CompanyModificationDto companyModificationDto) {
        companyService.updateCompany(companyModificationDto);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/{companyId}")
    public ResponseEntity getCompanyInfo(@PathVariable Long companyId) {
        log.info("company ID: {}", companyId);
        CompanyInfoDto companyInfoDto = companyService.findCompanyById(companyId);
        return ResponseEntity.ok(companyInfoDto);
    }

    @GetMapping("/{companyId}/employees")
    public ResponseEntity getEmployeeInfo(@PathVariable Long companyId) {
        log.info("company ID: {}", companyId);
        List<EmployeeGetDto> employees = companyService.findAllEmployeeByCompanyId(companyId);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{companyId}/available-employees")
    public ResponseEntity getAvailableEmployees(@PathVariable Long companyId,
                                                @RequestParam("projectId") @NotNull Long projectId) {
        log.info("Project ID: {}", projectId);
        log.info("Company ID: {}", companyId);
        List<AvailableEmployeesGetDto> employees = companyService.findAvailableEmployees(companyId, projectId);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{companyId}/cards")
    public ResponseEntity getCardsTodoItems(@PathVariable Long companyId, @RequestParam("userId") @NotNull Long userId) {
        log.info("Company ID: {}", companyId);
        List<List<CardTodoItemDto>> upcomingTodoItemDtoList = companyService.findTodoItemCardList(companyId, userId);
        return ResponseEntity.ok(upcomingTodoItemDtoList);
    }

    @PostMapping("/{companyId}/invitation")
    @ApiOperation(value = "Generate an invitation for user to join a company, if email given, send the link by email")
    @PreAuthorize("hasPermission(#companyId, 'Company Manager')")
    public ResponseEntity getInvitationLink(@PathVariable Long companyId,
                                            @Valid @RequestBody CompanyInvitedAccountDto accountDto,
                                            @RequestParam(required = false) String email
    ) {
        if (email == null) {
            return ResponseEntity.ok(companyService.generateInvitationLink(companyId, accountDto));
        } else {
            companyService.sendInvitationLink(companyId, accountDto);
            return ResponseEntity.ok("Email has been sent");
        }
    }
}
