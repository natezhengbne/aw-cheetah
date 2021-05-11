package com.asyncworking.services;

import com.asyncworking.dtos.MessageBoardPostDto;
import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.MessageBoardNotFoundException;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.MessageBoardRepository;
import com.asyncworking.repositories.MessageRepository;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.utility.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageMapper messageMapper;

    private final ProjectRepository projectRepository;

    private final MessageBoardRepository messageBoardRepository;

    private final MessageRepository messageRepository;

    @Transactional
    public Long createMessageBoard (MessageBoardPostDto messageBoardPostDto) {
       MessageBoard messageBoard = buildMessageBoard(fetchProjectById(messageBoardPostDto.getProjectId()));
       log.info("create a Message Board with Id" + messageBoard.getId());
       messageBoardRepository.save(messageBoard);

        return messageBoard.getId();
    }

    public MessageBoard buildMessageBoard(Project project) {
        return MessageBoard.builder()
                .project(project)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private Project fetchProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find project by id:" + projectId));
    }

    public MessageGetDto createMessage(MessagePostDto messagePostDto) {
        Message message = messageMapper.toEntity(messagePostDto);
        message.setMessageBoard(fetchMessageBoardById(messagePostDto.getMessageBoardId()));
        message.setCreatedTime(OffsetDateTime.now(ZoneOffset.UTC));
        message.setUpdatedTime(OffsetDateTime.now(ZoneOffset.UTC));
        message.setPostTime(OffsetDateTime.now(ZoneOffset.UTC));

        log.info("create a new message : " + messagePostDto.getMessageTitle());
        Message savedMessage = messageRepository.save(message);
        return messageMapper.fromEntity(savedMessage);
    }
    private MessageBoard fetchMessageBoardById(Long messageBoardId) {
        return messageBoardRepository
                .findById(messageBoardId)
                .orElseThrow(() -> new MessageBoardNotFoundException("Cannot find messageBoard by id: " + messageBoardId));
    }

}
