package com.asyncworking.services;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoList;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.TodoListRepository;
import com.asyncworking.utility.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;


@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoListRepository todoListRepository;

    private final ProjectRepository projectRepository;

    private final TodoMapper todoMapper;


    @Transactional
    public Long createTodoList(TodoListDto todoListDto) {
        TodoList newTodoList = mapTodoListDtoToEntity(todoListDto,
                fetchProjectById(todoListDto.getProjectId()));
        log.info("create a new TodoList: " + newTodoList.getTodoListTitle());
        todoListRepository.save(newTodoList);
        return newTodoList.getId();
    }

    private TodoList mapTodoListDtoToEntity(TodoListDto todoListDto, Project project){
        return TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle(todoListDto.getTodoListTitle())
                .details(todoListDto.getDetails())
                .docURL(todoListDto.getDocURL())
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
    }

    private Project fetchProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find project by id:" + projectId));
    }

    public List<TodoListDto> findRequiredNumberTodoListsByProjectId(Long projectId, Integer quantity) {
        return todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(projectId, quantity).stream()
                .map(todoMapper::fromEntity)
                .collect(Collectors.toList());
    }

    public TodoListDto findTodoListById(Long id) {
        return todoMapper.fromEntity(todoListRepository.findById(id)
                .orElseThrow(() -> new TodoListNotFoundException("Cannot find todoList by id: " + id)));
    }
}
