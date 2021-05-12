package com.asyncworking.services;

import com.asyncworking.dtos.TodoBoardDto;
import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.TodoBoardNotFoundException;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoBoard;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.TodoBoardRepository;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.repositories.TodoListRepository;
import com.asyncworking.utility.mapper.TodoMapper;
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
public class TodoService {

    private final TodoListRepository todoListRepository;

    private final TodoBoardRepository todoBoardRepository;

    private final TodoItemRepository todoItemRepository;

    private final ProjectRepository projectRepository;

    private final TodoMapper todoMapper;

    @Transactional
    public Long createTodoBoard(TodoBoardDto todoBoardDto) {
        TodoBoard newTodoBoard = buildTodoBoard(fetchProjectById(todoBoardDto.getProjectId()));
        log.info("create a todoBoard with Id: " + newTodoBoard.getId());
        todoBoardRepository.save(newTodoBoard);
        return newTodoBoard.getId();
    }

    private TodoBoard buildTodoBoard(Project project) {
        return TodoBoard.builder()
                .project(project)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private Project fetchProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find project by id:" + projectId));
    }

    @Transactional
    public Long createTodoList(TodoListDto todoListDto) {
        TodoList newTodoList = buildTodoList(todoListDto, fetchTodoBoardById(todoListDto.getTodoBoardId()));
        log.info("create a new TodoList: " + newTodoList.getTodoListTitle());
        todoListRepository.save(newTodoList);
        return newTodoList.getId();
    }

    private TodoList buildTodoList(TodoListDto todoListDto, TodoBoard todoBoard) {
        return TodoList.builder()
                .companyId(todoListDto.getCompanyId())
                .todoBoard(todoBoard)
                .companyId(todoListDto.getCompanyId())
                .projectId(todoBoard.getProject().getId())
                .todoListTitle(todoListDto.getTodoListTitle())
                .details(todoListDto.getDetails())
                .docURL(todoListDto.getDocURL())
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private TodoBoard fetchTodoBoardById(Long todoBoardId) {
        return todoBoardRepository
                .findById(todoBoardId)
                .orElseThrow(() -> new TodoBoardNotFoundException("Cannot find todoBoard by id: " + todoBoardId));
    }

    public List<TodoListDto> findTodoListsByProjectId(Long projectId) {
        List<TodoList> todoLists = todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(projectId);
        List<TodoListDto> todoListDtoS = new ArrayList<>();
        todoLists.forEach(todoList -> todoListDtoS.add(todoMapper.mapEntityToTodoListDto(todoList)));
        return todoListDtoS;
    }

    @Transactional
    public Long createTodoItem(TodoItemPostDto todoItemPostDto) {
        TodoItem newTodoItem = buildTodoItem(todoItemPostDto, getTodoItemById(todoItemPostDto.getTodoListId()));
        log.info("create a item with id " + newTodoItem.getTodoItemId());
        todoItemRepository.save(newTodoItem);
        return newTodoItem.getTodoItemId();
    }

    private TodoItem buildTodoItem(TodoItemPostDto todoItemPostDto, TodoList todoList) {
        return TodoItem.builder()
                .eventDocUrl(todoItemPostDto.getEventDocUrl())
                .companyId(todoList.getCompanyId())
                .projectId(todoList.getProjectId())
                .content(todoItemPostDto.getContent())
                .docUrl(todoList.getDocURL())
                .description(todoItemPostDto.getDescription())
                .completed(Boolean.TRUE)
                .dueDate(todoItemPostDto.getDueDate())
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private TodoList getTodoItemById(Long todoItemId) {
        return todoListRepository
                .findById(todoItemId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find todoItem by id:" + todoItemId));
    }
}
