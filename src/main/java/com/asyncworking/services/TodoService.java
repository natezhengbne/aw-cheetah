package com.asyncworking.services;

import com.asyncworking.dtos.TodoListDto;
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

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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

    public List<TodoListDto> findRequiredNumberTodoListsByProjectId(Long projectId, Integer quantity) {
        return todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(projectId, quantity).stream()
                .map(todoMapper::fromEntity)
                .collect(Collectors.toList());
    }

    public TodoListDto findTodoListById(Long id) {
        return todoMapper.fromEntity(todoListRepository.findById(id)
                .orElseThrow(() -> new TodoListNotFoundException("Cannot find todoList by id: " + id)));
    }

    @Transactional
    public Long createTodoItem(@Valid TodoItemPostDto todoItemPostDto) {
        TodoItem todoItem = todoMapper.toEntity(todoItemPostDto);
        TodoList todoList = todoListRepository.findById(todoItemPostDto.getTodoListId())
                .orElseThrow(() -> new TodoListNotFoundException("Cannot find todoList by id: " + todoItemPostDto.getTodoListId()));
        todoItem.setTodoList(todoList);
        todoItem.setCompanyId(todoList.getCompanyId());
        todoItem.setProjectId(todoList.getProject().getId());
        todoItem.setCompleted(Boolean.FALSE);
        todoItem.setCreatedTime(OffsetDateTime.now(UTC));
        todoItem.setUpdatedTime(OffsetDateTime.now(UTC));
//        TodoItem todoItem = buildTodoItem(todoItemPostDto, fetchTodoListById(todoItemPostDto.getTodoListId()));
//        log.info("todoItem sss:" + todoItem);
//        todoItemRepository.save(todoItem);
//        TodoItem todoItem = buildTodoItem(todoItemPostDto, todoListRepository.findById(todoItemPostDto.getTodoListId()).get());
        log.info("create a item with id " + todoItem.getTodoItemId());
        TodoItem savedTodoItem = todoItemRepository.save(todoItem);
        return savedTodoItem.getTodoItemId();
    }

//    private TodoItem buildTodoItem(TodoItemPostDto todoItemPostDto, TodoList todoList){
//        return TodoItem.builder()
//                .todoList(todoList)
//                .companyId(todoList.getCompanyId())
//                .projectId(todoList.getProject().getId())
//                .completed(Boolean.FALSE)
//                .createdTime(OffsetDateTime.now(UTC))
//                .updatedTime(OffsetDateTime.now(UTC))
//                .build();
//    }

//    public List<TodoListDto> getTodoListAndTodoItem(Long todoListId, Integer quantity) {
//        return todoListRepository.findTodoItemAndList(todoListId, quantity).stream()
//                .map(todoMapper::fromEntity)
//                .collect(Collectors.toList());
//    }

//    private TodoList fetchTodoListById(Long todoListId) {
//        return todoListRepository
//                .findById(todoListId)
//                .orElseThrow(() -> new ProjectNotFoundException("Cannot find todoList by id:" + todoListId));
//    }
}
