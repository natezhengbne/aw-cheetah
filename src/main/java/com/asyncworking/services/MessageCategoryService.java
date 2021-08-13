package com.asyncworking.services;

import com.asyncworking.dtos.MessageCategoryGetDto;
import com.asyncworking.dtos.MessageCategoryPostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.MessageCategory;
import com.asyncworking.models.Project;
import com.asyncworking.repositories.MessageCategoryRepository;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.utility.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageCategoryService {

    private final MessageMapper messageMapper;

    private final ProjectRepository projectRepository;

    private final MessageCategoryRepository messageCategoryRepository;

    public MessageCategoryGetDto createMessageCategory(MessageCategoryPostDto messageCategoryPostDto) {
        MessageCategory messageCategory = messageMapper.toCategoryEntity(messageCategoryPostDto,
                fetchProjectById(messageCategoryPostDto.getProjectId()));
        MessageCategory savedMessageCategory = messageCategoryRepository.save(messageCategory);
        log.info("create a new message category: " + messageCategoryPostDto.getCategoryName());
        MessageCategoryGetDto messageCategoryGetDto = messageMapper.fromCategoryEntity(savedMessageCategory);
        return messageCategoryGetDto;
    }

    public MessageCategoryGetDto createDefaultMessageCategory(Project project, String categoryName, String emoji) {
        MessageCategory messageCategory = messageMapper.toCategoryEntity(project, categoryName, emoji);
        MessageCategory savedMessageCategory = messageCategoryRepository.save(messageCategory);
        log.info("create default message category: " + categoryName);
        MessageCategoryGetDto messageCategoryGetDto = messageMapper.fromCategoryEntity(savedMessageCategory);
        return messageCategoryGetDto;
    }

    private Project fetchProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find project by id: " + projectId));
    }

    public List<MessageCategoryGetDto> findMessageCategoryListByCompanyIdAndProjectId(Long companyId, Long projectId) {
        List<MessageCategory> messageCategoryList = messageCategoryRepository.findByCompanyIdAndProjectId(companyId, projectId);

        return messageCategoryList.stream()
                .map(messageCategory -> messageMapper.fromCategoryEntity(messageCategory)).collect(Collectors.toList());
    }
}
