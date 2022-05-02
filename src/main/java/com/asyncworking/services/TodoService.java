package com.asyncworking.services;

import com.asyncworking.dtos.TodoListDto;

import com.asyncworking.dtos.todoitem.AssignedPeopleGetDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.dtos.todoitem.TodoItemMoveDto;
import com.asyncworking.dtos.todoitem.TodoItemPageDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.dtos.todoitem.TodoItemPutDto;
import com.asyncworking.dtos.todolist.TodoListPutDto;
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
import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.time.OffsetDateTime;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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
                .sorted(Comparator.comparingInt(TodoList::getListOrder))
                .map(todoList -> todoMapper.fromTodoListEntity(todoList, todoMapper.todoItemsToTodoItemGetDtos(todoList.getTodoItems())))
                .collect(Collectors.toList());
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
        savedTodoItem.setItemOrder(savedTodoItem.getId().intValue());
        savedTodoItem.setPendingId(todoItemPostDto.getTodoListId());
        todoItemRepository.save(savedTodoItem);
        log.info("created a item with id: {} ", savedTodoItem.getId());
        return savedTodoItem.getId();
    }

    public Boolean changeTodoItemCompleted(Long companyId, Long projectId, Long id, boolean completed) {
        TodoItem todoItem = findTodoItemByCompanyIdAndProjectIdAndId(companyId, projectId, id);
        Project project = findProjectByCompanyIdAndProjectId(companyId, projectId);
        TodoList todoList;
        if (completed){
            todoList = findTodoListByCompanyIdAndProjectIdAndId(companyId, projectId, project.getDoneListId());
        } else {
            todoList = findTodoListByCompanyIdAndProjectIdAndId(companyId, projectId, todoItem.getPendingId());
        }
        todoItem.setTodoList(todoList);
        log.info("todoItem origin completed status: " + todoItem.getCompleted());
        todoItem.setCompleted(completed);
        todoItem.setCompletedTime();
        todoItem.setItemOrder(findHigestOrder(todoList) + 1);
        todoItemRepository.save(todoItem);
        return todoItem.getCompleted();
    }

    private int findHigestOrder(TodoList todoList){
        if (todoList.getTodoItems().size() == 0) {
            return -1;
        }
        TodoItem todoItem = todoList.getTodoItems().stream().max(Comparator.comparing(TodoItem::getItemOrder))
                           .orElseThrow(() ->
                                    new TodoItemNotFoundException("Cannot find higestOrderTodoItem by given todoList: " + todoList));
        return todoItem.getItemOrder();
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

    private TodoList findTodoListById(Long todoListId) {
        return todoListRepository.findById(todoListId)
                .orElseThrow(() -> new TodoListNotFoundException("Cannot find TodoList by id: " + todoListId));
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
        return userEntityList.stream().map(userMapper::mapEntityToAssignedPeopleDto).collect(Collectors.toList());
    }

    private List<TodoItem> findTodoItemByGivenMoveItems(List<TodoItemMoveDto> moveItems) {
        List<Long> moveItemsIds = moveItems.stream().map(TodoItemMoveDto::getTodoItemId).collect(Collectors.toList());
        return todoItemRepository.findAllById(moveItemsIds);
    }

    private List<TodoList> findTodoListByGivenMoveLists(List<TodoListPutDto> moveLists) {
        List<Long> moveListIds = moveLists.stream().map(TodoListPutDto::getId).collect(Collectors.toList());
        return todoListRepository.findAllById(moveListIds);
    }

    public List<Long> reorderTodoList(List<TodoListPutDto> moveLists) {
        System.out.println(moveLists);
        List<TodoList> todoLists = findTodoListByGivenMoveLists(moveLists);
        log.info(todoLists.toString());
        Map<Long, TodoList> todoListMap = findTodoListByGivenMoveLists(moveLists).stream()
                .collect(Collectors.toMap(TodoList::getId, todoList -> todoList));

        AtomicReference<Integer> listOrderRef = new AtomicReference<>(0);
        moveLists.stream().forEach(moveList -> {
            TodoList todoList = todoListMap.get(moveList.getId());
            if (todoList.getListOrder() != listOrderRef.get()){
                todoList.setUpdatedTime(OffsetDateTime.now(UTC));
                todoList.setListOrder(listOrderRef.get());
            }
            int order = listOrderRef.get() + 1;
            listOrderRef.set(order);
        });

        log.info(todoLists.toString());
        todoListRepository.saveAll(todoLists);
        return todoLists.stream().map(TodoList::getId).collect(Collectors.toList());
    }

    private List<TodoItem> updateTodoItems(List<TodoItem> todoItems, List<TodoItemMoveDto> moveItems, TodoList todoList) {
        Map<Long, TodoItem> todoItemsMap = todoItems.stream()
                .collect(Collectors.toMap(TodoItem::getId, TodoItem -> TodoItem));
        AtomicReference<Integer> itemOrderRef = new AtomicReference<>(todoItems.size());
        moveItems.stream().forEach(moveItem -> {
            TodoItem todoItem = todoItemsMap.get(moveItem.getTodoItemId());
            todoItem.setTodoList(todoList);
            todoItem.setPendingId(todoList.getId());
            todoItem.setItemOrder(itemOrderRef.get());
            int order = itemOrderRef.get();
            itemOrderRef.set(--order);
        });
        return todoItems;
    }

    private TodoList updateTodoList(TodoList todoList, TodoListPutDto moveList){
        List<TodoItemMoveDto> moveItems = moveList.getTodoItems();
        List<TodoItem> todoItems = findTodoItemByGivenMoveItems(moveItems);
        List<TodoItem> updatedTodoItems = updateTodoItems(todoItems, moveItems, todoList);
        todoList.setTodoItems(updatedTodoItems);
        todoList.setUpdatedTime(OffsetDateTime.now(UTC));
        return todoList;
    }

    private List<Long> updateTodoLists(List<TodoListPutDto> moveLists) {
        log.info(moveLists.toString());
        Map<Long, TodoList> todoListMap = findTodoListByGivenMoveLists(moveLists).stream()
                .collect(Collectors.toMap(TodoList::getId, todoList -> todoList));

        moveLists.stream().forEach(moveList -> {
            TodoList todoList = todoListMap.get(moveList.getId());
            TodoList updatedTodoList = updateTodoList(todoList, moveList);
            todoListMap.put(moveList.getId(), updatedTodoList);
        });

        todoListRepository.saveAll(todoListMap.values());
        return todoListMap.entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Long reorderTodoItems(TodoListPutDto moveList) {
        TodoList todoList = findTodoListById(moveList.getId());
        TodoList updateTodoList = updateTodoList(todoList, moveList);
        todoListRepository.save(updateTodoList);
        return updateTodoList.getId();
    }

    public List<Long> moveTodoItems(List<TodoListPutDto> moveLists){
        if (moveLists.size() == 1){
            log.info("reorder todoItem");
            return Collections.singletonList(reorderTodoItems(moveLists.get(0)));
        } else {
            log.info("move todoItem");
            return updateTodoLists(moveLists);
        }
    }

}
