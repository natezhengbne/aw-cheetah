package com.asyncworking.services;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.MessageRepository;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.utility.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageMapper messageMapper;

    private final ProjectRepository projectRepository;

    private final MessageRepository messageRepository;


    @Transactional
    public MessageGetDto createMessage(MessagePostDto messagePostDto) {
        Message message = messageMapper.toEntity(messagePostDto);
        message.setProject(fetchProjectById(messagePostDto.getProjectId()));
        message.setCreatedTime(OffsetDateTime.now(ZoneOffset.UTC));
        message.setUpdatedTime(OffsetDateTime.now(ZoneOffset.UTC));
        message.setPostTime(OffsetDateTime.now(ZoneOffset.UTC));

        log.info("create a new message : " + messagePostDto.getMessageTitle());
        Message savedMessage = messageRepository.save(message);
        return messageMapper.fromEntity(savedMessage);
    }
    private Project fetchProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find project by id: " + projectId));
    }
    public List<MessageGetDto> findMessageListByProjectId(Long projectId) {
        return messageRepository.findMessageByProjectId(projectId).stream()
                .map(message -> messageMapper.fromEntity(message))
                .collect(Collectors.toList());
    }

}
