package com.asyncworking.controllers;

import com.asyncworking.dtos.MessageCategoryGetDto;
import com.asyncworking.dtos.MessageCategoryPostDto;
import com.asyncworking.services.MessageCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("companies/{companyId}/projects/{projectId}")
public class MessageCategoryController {
    private final MessageCategoryService messageCategoryService;

    @GetMapping("/message-categories")
    public ResponseEntity<List<MessageCategoryGetDto>> getMessageCategoryList(@PathVariable Long companyId, @PathVariable Long projectId) {
        log.info("get projectId " + projectId + " :message category");
        return ResponseEntity.ok(messageCategoryService.findMessageCategoryListByCompanyIdAndProjectId(companyId, projectId));
    }

    @PutMapping("/categoryId/{categoryId}/edition")
    public ResponseEntity<?> editMessageCategory
            (@PathVariable Long categoryId, @Valid @RequestBody MessageCategoryGetDto messageCategoryGetDto) {
        messageCategoryService.editMessageCategory(categoryId, messageCategoryGetDto);
        return ResponseEntity.ok("successfully changed");
    }

    @PostMapping("/creation")
    public ResponseEntity<MessageCategoryGetDto> createMessageCategory(@Valid @RequestBody MessageCategoryPostDto messageCategoryPostDto) {
        return ResponseEntity.ok(messageCategoryService.createMessageCategory(messageCategoryPostDto));
    }
}
