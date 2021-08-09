package com.asyncworking.controllers;

import com.asyncworking.dtos.MessageCategoryGetDto;
import com.asyncworking.services.MessageCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("companies/{companyId}/projects/{projectId}")
//Todo
public class MessageCategoryController {
    private final MessageCategoryService messageCategoryService;

    @GetMapping("/message-categories")
    public ResponseEntity<List<MessageCategoryGetDto>> getMessageCategoryList(@PathVariable Long companyId, @PathVariable Long projectId) {
        log.info("get projectId " + projectId + " :message category");
        return ResponseEntity.ok(messageCategoryService.findMessageCategoryListByCompanyIdAndProjectId(companyId, projectId));
    }
}
