package com.asyncworking.services;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.repositories.TodoListRepository;
import com.asyncworking.utility.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;


@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoListRepository todoListRepository;

    private final TodoItemRepository todoItemRepository;

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

//    public List<TodoListDto> findRequiredNumberTodoListsByProjectId(Long projectId, Integer quantity) {
//        return todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(projectId, quantity).stream()
//                .map(todoMapper::fromEntity)
//                .collect(Collectors.toList());
//    }

    public List<TodoListDto> findRequiredNumberTodoListsByProjectId(Long projectId, Integer quantity) {
        return todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(projectId, quantity).stream()
                .map(todoList -> mapTodoListDtoFromEntity(todoList))
                .collect(Collectors.toList());
    }


//    public TodoListDto findTodoListById(Long id) {
//        return todoMapper.fromEntity(todoListRepository.findById(id)
//                .orElseThrow(() -> new TodoListNotFoundException("Cannot find todoList by id: " + id)));
//    }

    public TodoListDto findTodoListById(Long id) {
        TodoList todoList = todoListRepository.findById(id)
                .orElseThrow(() -> new TodoListNotFoundException("Cannot find todoList by id: " + id));
        return mapTodoListDtoFromEntity(todoList);
    }

    @Transactional
    public Long createTodoItem(@Valid TodoItemPostDto todoItemPostDto) {
        TodoItem todoItem = todoMapper.toEntity(todoItemPostDto);
        TodoList todoList = todoListRepository.findById(todoItemPostDto.getTodolistId())
                .orElseThrow(() -> new TodoListNotFoundException("Cannot find todoList by id: " + todoItemPostDto.getTodolistId()));
        todoItem.setTodoList(todoList);
        todoItem.setCompanyId(todoList.getCompanyId());
        todoItem.setProjectId(todoList.getProject().getId());
        todoItem.setCompleted(Boolean.FALSE);
        todoItem.setCreatedTime(OffsetDateTime.now(UTC));
        todoItem.setUpdatedTime(OffsetDateTime.now(UTC));
        todoItemRepository.save(todoItem);
        log.info("created a item with id " + todoItem.getId());
        return todoItem.getId();
    }

    public List<TodoItemGetDto> findTodoItemsByTodoListIdOrderByCreatedTime(Long todoListId) {
        return todoItemRepository.findTodoItemListByTodoListIdOrderByCreatedTime(todoListId).stream()
                .map(todoMapper::fromEntity)
                .collect(Collectors.toList());
    }

    private TodoListDto mapTodoListDtoFromEntity(TodoList todoList) {
        return TodoListDto.builder()
                .id(todoList.getId())
                .projectId(todoList.getProject().getId())
                .todoListTitle(todoList.getTodoListTitle())
                .details(todoList.getDetails())
                .docURL(todoList.getDocURL())
                .todoItemGetDtos(findTodoItemsByTodoListIdOrderByCreatedTime(todoList.getId()))
                .build();
    }
}
