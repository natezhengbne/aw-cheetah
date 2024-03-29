package com.asyncworking.services;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.MessageCategoryNotFoundException;
import com.asyncworking.exceptions.MessageNotFoundException;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.Message;
import com.asyncworking.models.MessageCategory;
import com.asyncworking.models.Project;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.MessageCategoryRepository;
import com.asyncworking.repositories.MessageRepository;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageMapper messageMapper;

    private final ProjectRepository projectRepository;

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    private final MessageCategoryRepository messageCategoryRepository;

    @Transactional
    public MessageGetDto createMessage(Long companyId, Long projectId, MessagePostDto messagePostDto) {
        verifyMessagePostDto(companyId, projectId, messagePostDto);
        Message message = messageMapper.toEntity(messagePostDto,
                fetchProjectById(messagePostDto.getProjectId()),
                fetchMessageCategoryById(messagePostDto.getMessageCategoryId()));
        Message savedMessage = messageRepository.save(message);
        log.info("create a new message : " + messagePostDto.getMessageTitle());
        MessageGetDto messageGetDto = messageMapper.fromEntity(savedMessage,
                findUsernameByUserId(messagePostDto.getPosterUserId()));
        return messageGetDto;
    }

    public void verifyMessagePostDto(Long companyId, Long projectId, MessagePostDto messagePostDto) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new ProjectNotFoundException("There is no project: " + projectId));
        if (project.getCompanyId() != companyId) {
            throw new ProjectNotFoundException("There is no project: " + projectId + "in this company: " + companyId);
        }
        if (!userRepository.existsById(messagePostDto.getPosterUserId())) {
            throw new UserNotFoundException("Cannot find user by id: " + messagePostDto.getPosterUserId());
        }
    }

    private Project fetchProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find project by id: " + projectId));
    }

    private String findUsernameByUserId(Long userId) {
        return userRepository
                .findUserEntityById(userId)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user by id: " + userId))
                .getName();
    }

    public List<MessageGetDto> findMessageListByCompanyIdAndProjectId(Long companyId, Long projectId) {
        List<Message> messageList = messageRepository.findByCompanyIdAndProjectId(companyId, projectId);
        List<UserEntity> userEntityList = findUserEntityByMessageList(messageList);
        Map<Long, String> userIdNameMap = userEntityList.stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getName));

        return messageList.stream()
                .filter(m -> userIdNameMap.containsKey(m.getPosterUserId()))
                .map(message -> messageMapper.fromEntity(message, userIdNameMap.get(message.getPosterUserId()))).
                        collect(Collectors.toList());
    }

    private MessageCategory fetchMessageCategoryById(Long messageCategoryId) {
        if (messageCategoryId == null) {
            return null;
        }
        return messageCategoryRepository
                .findById(messageCategoryId)
                .orElseThrow(() -> new MessageCategoryNotFoundException("Cannot find message category by id: " + messageCategoryId));
    }

    public List<UserEntity> findUserEntityByMessageList(List<Message> messageList) {
        List<Long> userIds = messageList.stream()
                .map(Message::getPosterUserId).collect(Collectors.toList());
        return userRepository.findByIdIn(userIds)
                .orElseThrow(() -> new UserNotFoundException("cannot find user by id in " + userIds));
    }

    public MessageGetDto findMessageByCompanyIdAndProjectIdAndId(Long companyId, Long projectId, Long id) {
        return messageRepository.findByCompanyIdAndProjectIdAndId(companyId, projectId, id)
                .map(m -> messageMapper.fromEntity(m, findUsernameByUserId(m.getPosterUserId())))
                .orElseThrow(() -> new MessageNotFoundException("cannot find message by id " + id));
    }
}
