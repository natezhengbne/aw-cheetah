package com.asyncworking.services;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.*;
import com.asyncworking.models.*;
import com.asyncworking.repositories.*;
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

    private final CompanyRepository companyRepository;

    private final MessageCategoryRepository messageCategoryRepository;

    @Transactional
    public MessageGetDto createMessage(MessagePostDto messagePostDto) {
        verifyMessagePostDto(messagePostDto);
        if (messagePostDto.getMessageCategoryId() != null) {
            Message message = messageMapper.toEntity(messagePostDto,
                    fetchProjectById(messagePostDto.getProjectId()),
                    fetchMessageCategoryById(messagePostDto.getMessageCategoryId()));
            Message savedMessage = messageRepository.save(message);
            log.info("create a new message : " + messagePostDto.getMessageTitle());
            MessageGetDto messageGetDto = messageMapper.fromEntity(savedMessage,
                    findUsernameByUserId(messagePostDto.getPosterUserId()));
            return messageGetDto;
        } else {
            Message message = messageMapper.toEntity(messagePostDto,
                    fetchProjectById(messagePostDto.getProjectId()));
            Message savedMessage = messageRepository.save(message);
            log.info("create a new message : " + messagePostDto.getMessageTitle());
            MessageGetDto messageGetDto = messageMapper.fromEntity(savedMessage,
                    findUsernameByUserId(messagePostDto.getPosterUserId()));
            return messageGetDto;
        }
    }

    public void verifyMessagePostDto(MessagePostDto messagePostDto) {
        if (!companyRepository.existsById(messagePostDto.getCompanyId())) {
            throw new CompanyNotFoundException("Cannot find company by id:" + messagePostDto.getCompanyId());
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

    public List<MessageGetDto> findMessageListByProjectId(Long projectId) {
        List<Message> messageList = messageRepository.findByProjectId(projectId);
        List<UserEntity> userEntityList = findUserEntityByMessageList(messageList);
        Map<Long, String> userIdNameMap = userEntityList.stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getName));

        return messageList.stream()
                .filter(m -> userIdNameMap.containsKey(m.getPosterUserId()))
                .map(message -> messageMapper.fromEntity(message, userIdNameMap.get(message.getPosterUserId()))).
                        collect(Collectors.toList());
    }

    private MessageCategory fetchMessageCategoryById(Long messageCategoryId) {
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

    public MessageGetDto findMessageById(Long id) {
        return messageRepository.findById(id)
                .map(m -> messageMapper.fromEntity(m, findUsernameByUserId(m.getPosterUserId())))
                .orElseThrow(() -> new MessageNotFoundException("cannot find message by id " + id));
    }
}
