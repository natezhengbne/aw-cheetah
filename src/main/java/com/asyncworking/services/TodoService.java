package com.asyncworking.services;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemPageDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.TodoItemNotFoundException;
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
import java.util.List;
import java.util.stream.Collectors;

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
        TodoList newTodoList = todoMapper.toTodoListEntity(todoListDto,
                findProjectById(todoListDto.getProjectId()));
        log.info("create a new TodoList: " + newTodoList.getTodoListTitle());
        todoListRepository.save(newTodoList);
        return newTodoList.getId();
    }

    public List<TodoListDto> findRequiredNumberTodoListsByProjectId(Long projectId, Integer quantity) {
        return todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(projectId, quantity).stream()
                .map(todoList -> todoMapper.fromTodoListEntity(todoList, findTodoItemsByTodoListIdOrderByCreatedTime(todoList.getId())))
                .collect(Collectors.toList());
    }

    public TodoListDto fetchSingleTodoList(Long id) {
        return todoMapper.fromTodoListEntity(findTodoListById(id),  findTodoItemsByTodoListIdOrderByCreatedTime(id));
    }

    @Transactional
    public Long createTodoItem(@Valid TodoItemPostDto todoItemPostDto) {
        TodoItem todoItem = todoMapper.toTodoItemEntity(todoItemPostDto, findTodoListById(todoItemPostDto.getTodolistId()));
        todoItemRepository.save(todoItem);
        log.info("created a item with id " + todoItem.getId());
        return todoItem.getId();
    }

    public List<TodoItemGetDto> findTodoItemsByTodoListIdOrderByCreatedTime(Long todoListId) {
        return todoItemRepository.findByTodoListIdOrderByCreatedTime(todoListId).stream()
                .map(todoMapper::fromTodoItemEntity)
                .collect(Collectors.toList());
    }

    public TodoItemPageDto fetchTodoItemPageInfoByIds(Long todoItemId) {
        return todoMapper.fromTodoItemToTodoItemPageDto(findTodoItemById(todoItemId),
                findProjectById(findTodoItemById(todoItemId).getProjectId()));
    }

    private Project findProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find project by id:" + projectId));
    }

    private TodoList findTodoListById(Long todoListId) {
        return todoListRepository.findById(todoListId)
                .orElseThrow(() -> new TodoListNotFoundException("Cannot find todoList by id: " + todoListId));
    }

    private TodoItem findTodoItemById(Long todoItemId) {
        return todoItemRepository
                .findById(todoItemId)
                .orElseThrow(() -> new TodoItemNotFoundException("Cannot find TodoItem by id: " + todoItemId));
    }
}
