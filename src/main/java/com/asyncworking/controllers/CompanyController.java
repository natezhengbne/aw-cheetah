package com.asyncworking.controllers;

import com.asyncworking.dtos.*;
import com.asyncworking.dtos.todoitem.CardTodoItemDto;
import com.asyncworking.models.TodoItem;
import com.asyncworking.services.CompanyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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


    @PostMapping("/{companyId}/invite-company-users")
    @PreAuthorize("hasPermission(#companyId, 'Company Manager')")
    public ResponseEntity sendCompanyInvitationSQSMessage(@PathVariable Long companyId,
                                                          @Valid @RequestBody CompanyInvitedAccountDto accountDto)
            throws JsonProcessingException {
        companyService.sendCompanyInvitationToSQS(companyId, accountDto);
        return ResponseEntity.ok("success");
    }
}
