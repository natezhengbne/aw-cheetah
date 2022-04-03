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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {
    private static final String DONE = "Done";

    private final TodoListRepository todoListRepository;

    private final TodoItemRepository todoItemRepository;

    private final ProjectRepository projectRepository;

    private final UserService userService;

    private final TodoMapper todoMapper;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional
    public Long createTodoList(Long companyId, Long projectId, TodoListDto todoListDto) {
        TodoList newTodoList = todoMapper.toTodoListEntity(todoListDto, findProjectById(companyId, projectId));
        log.info("create a new TodoList: " + newTodoList.getTodoListTitle());
        todoListRepository.save(newTodoList);
        return newTodoList.getId();
    }

    @Transactional
    public boolean updateTodoListTitle(Long companyId, Long projectId, Long todoListId,
                                       @RequestBody String todoListTitle) {
        int res = todoListRepository.updateTodoListTitle(
                todoListId,
                companyId,
                projectId,
                todoListTitle,
                OffsetDateTime.now(UTC)
        );

        if (res == 0) {
            throw new TodoListNotFoundException("Cannot find todoList by id: " + todoListId);
        }
        return true;
    }

    public List<TodoListDto> findRequiredNumberTodoListsByCompanyIdAndProjectId(Long companyId, Long projectId, Integer quantity) {
        List<TodoListDto> todoListDtos = todoListRepository.
                findTodolistWithTodoItems(
                        companyId,
                        projectId,
                        PageRequest.of(0, quantity)).stream()
                .map(todoList -> todoMapper.fromTodoListEntity(todoList, todoMapper.todoItemsToTodoItemGetDtos(todoList.getTodoItems())))
                .collect(Collectors.toList());
        Optional<Project> project = projectRepository.findByCompanyIdAndId(companyId, projectId);
        for (TodoListDto todoListDto : todoListDtos) {
            if (project.isPresent() && todoListDto.getId().longValue() == project.get().getDoneListId().longValue()) {
                List<TodoItemGetDto> todoItems = todoListDto.getTodoItems();
                Collections.sort(todoItems);
                Collections.reverse(todoItems);
            }
        }
        return todoListDtos;
    }

    public TodoListDto fetchSingleTodoList(Long companyId, Long projectId, Long id) {
        return todoMapper.fromTodoListEntity(findTodoListByCompanyIdAndProjectIdAndId(companyId, projectId, id),
                findByCompanyIdAndProjectIdAndTodoListIdOrderByCreatedTimeDesc
                        (companyId, projectId, id));
    }


    public Long createTodoItem(Long companyId, Long projectId, @Valid TodoItemPostDto todoItemPostDto) {
        TodoList todoList = findTodoListByCompanyIdAndProjectIdAndId(companyId, projectId, todoItemPostDto.getTodoListId());
        TodoItem todoItem = todoMapper.toTodoItemEntity(todoItemPostDto, todoList);
        TodoItem savedTodoItem = todoItemRepository.save(todoItem);

        log.info("created a item with id: {} ", savedTodoItem.getId());
        return savedTodoItem.getId();
    }


    public Boolean changeTodoItemCompleted(Long companyId, Long projectId, Long id, boolean completed) {
        TodoItem todoItem = findTodoItemByCompanyIdAndProjectIdAndId(companyId, projectId, id);
        Project project = findProjectByCompanyIdAndProjectId(companyId, projectId);
        TodoList todoList = findTodoListByCompanyIdAndProjectIdAndId(companyId, projectId, project.getDoneListId());
        todoItem.setTodoList(todoList);
        log.info("todoItem origin completed status: " + todoItem.getCompleted());
        todoItem.setCompleted(completed);
        todoItem.setCompletedTime();
        todoItemRepository.save(todoItem);
        return todoItem.getCompleted();
    }

    public List<TodoItemGetDto> findByCompanyIdAndProjectIdAndTodoListIdOrderByCreatedTimeDesc(Long companyId,
                                                                                               Long projectId, Long todoListId) {
        return todoItemRepository.findByCompanyIdAndProjectIdAndTodoListIdOrderByCreatedTimeDesc(companyId, projectId, todoListId)
                .stream()
                .map(todoMapper::fromTodoItemEntity)
                .collect(Collectors.toList());
    }


    public TodoItemPageDto fetchTodoItemPageInfoByIds(Long companyId, Long projectId, Long todoItemId) {
        TodoItem todoItem = findTodoItemByCompanyIdAndProjectIdAndId(companyId, projectId, todoItemId);
        return todoMapper.fromTodoItemToTodoItemPageDto(todoItem,
                findProjectById(companyId, todoItem.getProjectId()),
                userService.findUserById(todoItem.getCreatedUserId()));
    }

    @Transactional
    public void updateTodoItemDetails(Long companyId, Long projectId, Long todoItemId, TodoItemPutDto todoItemPutDto) {
        int res = todoItemRepository.updateTodoItem(todoItemId,
                todoItemPutDto.getDescription(),
                todoItemPutDto.getPriority(),
                todoItemPutDto.getNotes(),
                todoItemPutDto.getOriginNotes(),
                todoItemPutDto.getDueDate(),
                companyId, projectId,
                todoItemPutDto.getSubscribersIds());
        if (res == 0) {
            throw new TodoItemNotFoundException("There is no todoItem id is " + todoItemId);
        }
    }

    private Project findProjectById(Long companyId, Long projectId) {
        return projectRepository
                .findByCompanyIdAndId(companyId, projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find project by id:" + projectId));
    }

    private TodoList findTodoListByCompanyIdAndProjectIdAndId(Long companyId, Long projectId, Long todoListId) {
        return todoListRepository.findByCompanyIdAndProjectIdAndId(companyId, projectId, todoListId)
                .orElseThrow(() -> new TodoListNotFoundException("Cannot find todoList by id: " + todoListId));
    }

    private TodoItem findTodoItemByCompanyIdAndProjectIdAndId(Long companyId, Long projectId, Long todoItemId) {
        return todoItemRepository
                .findByCompanyIdAndProjectIdAndId(companyId, projectId, todoItemId)
                .orElseThrow(() -> new TodoItemNotFoundException("Cannot find TodoItem by id: " + todoItemId));
    }

    private Project findProjectByCompanyIdAndProjectId(Long companyId, Long projectId) {
        return projectRepository.findByCompanyIdAndId(companyId, projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find project by id: " + projectId));
    }

    private TodoList findTodoListById(Long todoListId){
        return todoListRepository.findById(todoListId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find TodoList by id: " + todoListId));
    }

    private TodoItem findTodoItemById(Long todoItemId){
        return todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find TodoItem by id: " + todoItemId));
    }



    public List<AssignedPeopleGetDto> findAssignedPeople(Long companyId, Long projectId, Long todoItemId) {

        String subscribersIds = todoItemRepository.findSubscribersIdsByProjectIdAndId(companyId, projectId, todoItemId);
        if (subscribersIds.length() == 0) {
            return null;
        }
        List<Long> idList = Arrays.asList(subscribersIds
                .split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        List<UserEntity> userEntityList = userRepository.findByIdIn(idList)
                .orElseThrow(() -> new UserNotFoundException("cannot find user by id in " + idList));
        return userEntityList.stream().map(userEntity -> userMapper.mapEntityToAssignedPeopleDto(userEntity)).collect(Collectors.toList());
    }

    public void moveTodoItem (Long todoItemId, Long targetTodoListId, Long targetTodoItemIndex){
        TodoItem todoItem = findTodoItemById(todoItemId);
        Long targetIndex = targetTodoItemIndex;
        TodoList targetTodoList = findTodoListById(targetTodoListId);
        List<TodoItem> targetTodoItemList = targetTodoList.getTodoItems();
        LinkedList<TodoItem> linkedList = new LinkedList<>();
        linkedList.addAll(targetTodoItemList);
        int linkedListSize = linkedList.size();
        for(int i = 0; i < linkedListSize; i++){
            if(targetIndex == linkedList.get(i).getId()){
                linkedList.add(i,todoItem);
            }
        }
        List<TodoItem> newTodoItems = linkedList;
        targetTodoList.setTodoItems(newTodoItems);
        for(int i = 0; i < linkedList.size(); i++){
            TodoItem item = linkedList.get(i);
            item.setTodoList(targetTodoList);
            item.setItemOrder((long) i);
            todoItemRepository.save(item);
        }
//        targetTodoList.setTodoItems(newTodoItems);
//        todoListRepository.save(targetTodoList);
        log.info(linkedList.toString());
    }

}
