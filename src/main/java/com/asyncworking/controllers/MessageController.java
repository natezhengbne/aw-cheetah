package com.asyncworking.controllers;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/projects/{projectId}")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/messages")
    public ResponseEntity<List<MessageGetDto>> getMessageList(@PathVariable Long projectId) {
        log.info("get projectId " + projectId + " :messages");
        return ResponseEntity.ok(messageService.findMessageListByProjectId(projectId));
    }

    @PostMapping("/messages")
    @PreAuthorize("hasAuthority('edit message')")
    public ResponseEntity<MessageGetDto> createMessage(@PathVariable Long projectId, @Valid @RequestBody MessagePostDto messagePostDto) {
        return ResponseEntity.ok(messageService.createMessage(messagePostDto));
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<MessageGetDto> getMessages(@PathVariable Long projectId, @PathVariable Long messageId) {
        log.info("get messageId " + messageId);
        return ResponseEntity.ok(messageService.findMessageById(messageId));
    }
}
