package com.asyncworking.controllers;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/projects/{projectId}/messageList")
    public ResponseEntity<List<MessageGetDto>> getMessageList (@PathVariable Long projectId) {
        return ResponseEntity.ok(messageService.findMessageListByProjectId(projectId));
    }

    @PostMapping("/message")
    public ResponseEntity<Long>  createMessage (@Valid @RequestBody MessagePostDto messagePostDto) {
        return ResponseEntity.ok(messageService.createMessage(messagePostDto));
    }

}
