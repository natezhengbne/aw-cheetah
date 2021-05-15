package com.asyncworking.controllers;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.services.MessageService;
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
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/projects/{projectId}/messageList")
    public ResponseEntity<List<MessageGetDto>> getMessageList (@PathVariable Long projectId) {
        log.info("get projectId " + projectId + " :messageList");
        return ResponseEntity.ok(messageService.findMessageListByProjectId(projectId));
    }

    @PostMapping("/message")
    public ResponseEntity<MessageGetDto>  createMessage (@Valid @RequestBody MessagePostDto messagePostDto) {
        return ResponseEntity.ok(messageService.createMessage(messagePostDto));
    }

}
