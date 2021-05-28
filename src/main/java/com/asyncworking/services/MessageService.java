package com.asyncworking.services;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.MessageNotFoundException;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.*;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageMapper messageMapper;

    private final ProjectRepository projectRepository;

    private final MessageRepository messageRepository;

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
        List<MessageGetDto> messageGetDtoList = new ArrayList<>();
        List<Message> messageList = messageRepository.findByProjectId(projectId);
        List<UserEntity> userEntityList = this.findUserEntityByMessageList(messageList);
        MessageGetDto messageGetDto = null;
        for (Message m : messageList){
            for (UserEntity u :userEntityList) {
                if (m.getPosterUserId() == u.getId()) {
                    messageGetDto = messageMapper.fromEntity(m);
                    messageGetDto.setPosterUser(u.getName());
                }
            }
            if (messageGetDto == null){
                throw new UserNotFoundException("cannot find user by id " + m.getPosterUserId());
            }
            messageGetDtoList.add(messageGetDto);
            messageGetDto = null;
        }

        return messageGetDtoList;
    }


    public List<UserEntity> findUserEntityByMessageList(List<Message> messageList) {
        List<Long> userId = new ArrayList<>();
        messageList.stream().forEach(message -> userId.add(message.getPosterUserId()));
        return userRepository.findByIdIn(userId)
                .orElseThrow(() -> new UserNotFoundException("cannot find user by id in " + userId.toString()));
    }

    public MessageGetDto findMessageById(Long id) {
        MessageGetDto messageGetDto = messageMapper.fromEntity(messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException("cannot find message by id " + id)));
        messageGetDto.setPosterUser(this.findUsernameByUserId(messageGetDto.getPosterUserId()));
        return messageGetDto;
    }
 }
