package com.asyncworking.controllers;

import com.asyncworking.dtos.EmployeeGetDto;
import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.dtos.ProjectModificationDto;
import com.asyncworking.services.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/companies/{companyId}/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity createProject(@Valid @RequestBody ProjectDto projectDto) {
        return ResponseEntity.ok(projectService.createProjectAndProjectUser(projectDto));
    }

    @GetMapping
    public ResponseEntity getProjectList(@PathVariable("companyId")
                                         @NotNull Long companyId,
                                         @RequestParam(value = "userId")
                                         @NotNull Long userId) {
        return ResponseEntity.ok(projectService.fetchAvailableProjectInfoList(companyId, userId));
    }
    @GetMapping("/{projectId}/project-info")
    public ResponseEntity getProjectInfo(@PathVariable("companyId") Long companyId, @PathVariable("projectId") @NotNull Long projectId) {
        log.info("projectId: {}", projectId);
        ProjectInfoDto projectInfoDto = projectService.fetchProjectInfoByProjectIdAndCompanyId(companyId, projectId);
        return ResponseEntity.ok(projectInfoDto);
    }

    @PutMapping("/{projectId}/project-info")
    public ResponseEntity updateProjectProfile(@PathVariable("companyId") Long companyId,
                                               @PathVariable("projectId") Long projectId,
                                               @Valid @RequestBody ProjectModificationDto projectModificationDto) {
        projectService.updateProjectInfo(companyId, projectId, projectModificationDto);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity getMembersByCompanyIdAndProjectId(@PathVariable("companyId") Long companyId,
                                                            @PathVariable("projectId") Long projectId) {
        log.info("Project ID: {}", projectId);
        List<EmployeeGetDto> members = projectService.findAllMembersByCompanyIdAndProjectId(companyId, projectId);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<?> addMembersByCompanyIdProjectIdAndUserId(@PathVariable("companyId") Long companyId,
                                                            @PathVariable("projectId") Long projectId,
                                                            @RequestParam("userIds") @NotNull List<Long> userIds) {
        log.info(" companyId{}, projectID: {}, userIds: {}", companyId, projectId, userIds);
        projectService.addProjectUsers(companyId, projectId, userIds);
        return ResponseEntity.ok("success");
    }
}

