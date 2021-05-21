package com.asyncworking.services;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.IMessageRepository;
import com.asyncworking.repositories.MessageRepository;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.UserRepository;
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

//    private final IMessageRepository iMessageRepository;

    private final UserRepository userRepository;


    @Transactional
    public MessageGetDto createMessage(MessagePostDto messagePostDto) {
        Message message = messageMapper.toEntity(messagePostDto);
        message.setProject(fetchProjectById(messagePostDto.getProjectId()));
        message.setCreatedTime(OffsetDateTime.now(ZoneOffset.UTC));
        message.setUpdatedTime(OffsetDateTime.now(ZoneOffset.UTC));
        message.setPostTime(OffsetDateTime.now(ZoneOffset.UTC));

        log.info("create a new message : " + messagePostDto.getMessageTitle());

        Message savedMessage = messageRepository.save(message);
        MessageGetDto messageGetDto = messageMapper.fromEntity(savedMessage);
        messageGetDto.setPosterUser(this.findUsernameByUserId(messagePostDto.getPosterUserId()));
        return messageGetDto;
    }


    private Project fetchProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find project by id: " + projectId));
    }

    private String findUsernameByUserId(Long userId) {
        return userRepository
                .findUserEntityById(userId)
                .orElseThrow(() -> new UserNotFoundException("cannot find user by id " + userId))
                .getName();

    }

    public List<MessageGetDto> findMessageListByProjectId(Long projectId) {
        List<MessageGetDto> messageGetDtoList = messageRepository.findMessageAndUserNameByProjectId(projectId).stream()
                .map(message -> mapImessageInfoToMessageGetDto(message))
                .collect(Collectors.toList());

        return messageGetDtoList;
    }

    public MessageGetDto mapImessageInfoToMessageGetDto(IMessageInfo iMessageInfo) {
        return MessageGetDto.builder()
                .posterUserId(iMessageInfo.getPosterUserId())
//                .postTime(iMessageInfo.getPostTime())
                .messageTitle(iMessageInfo.getMessageTitle())
                .build();
    }

}
