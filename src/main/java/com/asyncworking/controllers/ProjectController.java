package com.asyncworking.controllers;

import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.services.ProjectService;
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
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/project")
    public ResponseEntity<?> projectCreate(@Valid @RequestBody ProjectDto projectDto){
        return ResponseEntity.ok(projectService.createProjectAndProjectUser(projectDto));
    }

    @GetMapping("/project")
    public ResponseEntity<?> fetchAllProjectInfoList(@RequestParam("companyId")
                                                @NotNull Long companyId) {
        return ResponseEntity.ok(projectService.fetchProjectInfoListByCompanyId(companyId));
    }
}

