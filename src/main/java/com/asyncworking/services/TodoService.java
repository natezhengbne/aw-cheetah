package com.asyncworking.services;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.*;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.TodoItemNotFoundException;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.repositories.TodoListRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.TodoMapper;
import com.asyncworking.utility.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoListRepository todoListRepository;

    private final TodoItemRepository todoItemRepository;

    private final ProjectRepository projectRepository;

    private final UserService userService;

    private final TodoMapper todoMapper;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional
    public Long createTodoList(TodoListDto todoListDto) {
        TodoList newTodoList = todoMapper.toTodoListEntity(todoListDto,
                findProjectById(todoListDto.getProjectId()));
        log.info("create a new TodoList: " + newTodoList.getTodoListTitle());
        todoListRepository.save(newTodoList);
        return newTodoList.getId();
    }

    public List<TodoListDto> findRequiredNumberTodoListsByProjectId(Long projectId, Integer quantity) {
        return todoListRepository.findTodolistWithTodoItems(projectId, PageRequest.of(0, quantity)).stream()
                .map(todoList -> todoMapper.fromTodoListEntity(todoList, todoMapper.todoItemsToTodoItemGetDtos(todoList.getTodoItems())))
                .collect(Collectors.toList());
    }

    public TodoListDto fetchSingleTodoList(Long id) {
        return todoMapper.fromTodoListEntity(findTodoListById(id), findTodoItemsByTodoListIdOrderByCreatedTime(id));
    }

    @Transactional
    public Long createTodoItem(@Valid TodoItemPostDto todoItemPostDto) {
        TodoItem savedTodoItem = todoItemRepository.save(
                todoMapper.toTodoItemEntity(todoItemPostDto, findTodoListById(todoItemPostDto.getTodolistId())));
        log.info("created a item with id " + savedTodoItem.getId());
        return savedTodoItem.getId();
    }

    @Transactional
    public Boolean changeTodoItemCompleted(Long id) {
        TodoItem todoItem = findTodoItemById(id);
        log.info("todoItem origin completed status: " + todoItem.getCompleted());
        todoItem.setCompleted(!todoItem.getCompleted());
        todoItemRepository.save(todoItem);
        return todoItem.getCompleted();
    }

    public List<TodoItemGetDto> findTodoItemsByTodoListIdOrderByCreatedTime(Long todoListId) {
        return todoItemRepository.findByTodoListIdOrderByCreatedTimeDesc(todoListId).stream()
                .map(todoMapper::fromTodoItemEntity)
                .collect(Collectors.toList());
    }


    public TodoItemPageDto fetchTodoItemPageInfoByIds(Long todoItemId) {
        TodoItem todoItem = findTodoItemById(todoItemId);
        return todoMapper.fromTodoItemToTodoItemPageDto(todoItem,
                findProjectById(todoItem.getProjectId()),
                userService.findUserById(todoItem.getCreatedUserId()));
    }

    @Transactional
    public void updateTodoItemDetails(Long todoItemId, TodoItemPutDto todoItemPutDto) {
        int res = todoItemRepository.updateTodoItem(todoItemId,
                todoItemPutDto.getDescription(),
                todoItemPutDto.getNotes(),
                todoItemPutDto.getOriginNotes(),
                todoItemPutDto.getDueDate(),
                todoItemPutDto.getSubscribersIds());
        if (res != 1) {
            throw new TodoItemNotFoundException("There is no todoItem id is " + todoItemId);
        }
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


    public List<AssignedPeopleGetDto> findAssignedPeople(Long projectId, Long todoItemId) {

        String subscribersIds = todoItemRepository.findSubscribersIdsByProjectIdAndId(projectId, todoItemId);
        if (subscribersIds.length() == 0 || subscribersIds == null) {
            return null;
        }
        List<Long> idList = Arrays.asList(subscribersIds
                .split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        List<UserEntity> userEntityList = userRepository.findByIdIn(idList)
                .orElseThrow(() -> new UserNotFoundException("cannot find user by id in " + idList));
        return userEntityList.stream().map(userEntity -> userMapper.mapEntityToAssignedPeopleDto(userEntity)).collect(Collectors.toList());


    }


}
