package com.asyncworking.controllers;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemPageDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.dtos.todoitem.TodoItemPutDto;
import com.asyncworking.dtos.todolist.MoveTodoListDto;
import com.asyncworking.dtos.todolist.MovedItemsListDto;
import com.asyncworking.services.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("companies/{companyId}/projects/{projectId}")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todolists")
    public ResponseEntity<Long> createTodoList(@PathVariable Long companyId, @PathVariable Long projectId,
                                               @Valid @RequestBody TodoListDto todoListDto) {
        return ResponseEntity.ok(todoService.createTodoList(companyId, projectId, todoListDto));
    }

    @PutMapping("/todolists/{todolistId}")
    public ResponseEntity updateTodoListTitle(@PathVariable Long companyId, @PathVariable Long projectId,
                                              @PathVariable Long todolistId,
                                              @Valid @RequestBody TodoListDto todoListDto) {
        todoService.updateTodoListTitle(companyId, projectId, todolistId, todoListDto.getTodoListTitle());
        return ResponseEntity.ok("success");
    }

    @GetMapping("/todolists")
    public ResponseEntity<List<TodoListDto>> fetchTodoLists(@PathVariable Long companyId, @PathVariable Long projectId,
                                                            @NotNull @RequestParam("quantity") Integer quantity) {
        return ResponseEntity.ok(todoService.findRequiredNumberTodoListsByCompanyIdAndProjectId(companyId, projectId, quantity));
    }

    @GetMapping("/todolists/{todolistId}")
    public ResponseEntity<TodoListDto> fetchSingleTodoList(@PathVariable Long companyId, @PathVariable Long projectId,
                                                           @PathVariable Long todolistId) {
        log.info("todolistId:" + todolistId);
        return ResponseEntity.ok(todoService.fetchSingleTodoList(companyId, projectId, todolistId));
    }

    @PostMapping("/todolists/{todolistId}/todoitems")
    public ResponseEntity createTodoItem(@PathVariable Long companyId, @PathVariable Long projectId,
                                         @Valid @RequestBody TodoItemPostDto todoItemPostDto) {
        todoService.createTodoItem(companyId, projectId, todoItemPostDto);
        return ResponseEntity.ok("create todo item success");
    }

    @GetMapping("/todoitems/{todoitemId}")
    public ResponseEntity<TodoItemPageDto> getTodoItemPageInfo(@PathVariable Long companyId,
                                                               @PathVariable Long projectId,
                                                               @PathVariable Long todoitemId) {
        log.info("todoitemId:" + todoitemId);
        return ResponseEntity.ok(todoService.fetchTodoItemPageInfoByIds(companyId, projectId, todoitemId));
    }

    @PutMapping("/todoitems/{todoitemId}")
    public ResponseEntity<String> updateTodoItem(@PathVariable Long companyId, @PathVariable Long projectId,
                                                 @PathVariable Long todoitemId, @RequestBody TodoItemPutDto todoItemPutDto) {
        todoService.updateTodoItemDetails(companyId, projectId, todoitemId, todoItemPutDto);
        return ResponseEntity.ok("update success");
    }

    @PutMapping("/todoitems/{todoitemId}/completed")
    public ResponseEntity<?> changeTodoItemCompletedStatus(@PathVariable Long companyId,
                                                           @PathVariable Long projectId,
                                                           @PathVariable Long todoitemId,
                                                           @RequestParam(value = "completedStatus") boolean completed) {
        return ResponseEntity.ok(todoService.changeTodoItemCompleted(companyId, projectId, todoitemId, completed));
    }

    @GetMapping("/todoitems/{todoitemId}/assignees")
    public ResponseEntity<?> findAssignedPeopleById(@PathVariable Long companyId, @PathVariable Long projectId,
                                                    @PathVariable Long todoitemId) {
        return ResponseEntity.ok(todoService.findAssignedPeople(companyId, projectId, todoitemId));
    }

    @PutMapping("/todoitems/update-todolists")
    public ResponseEntity<?> reorderTodoLists(@Valid @RequestBody MoveTodoListDto movedLists)  {
        log.info(movedLists.toString());
        todoService.reorderTodoList(movedLists.getTodoLists());
        return ResponseEntity.ok("success");
    }

    @PutMapping("/todoitems/update-todoitems")
    public ResponseEntity<?> reorderTodoItems(@Valid @RequestBody MovedItemsListDto movedItemsList)  {
        log.info(movedItemsList.toString());
        todoService.reorderTodoItems(movedItemsList.getMovedItemsList());
        return ResponseEntity.ok("success");
    }

    @PutMapping("/todoitems/update-two-todolists")
    public ResponseEntity<?> moveTodoItems(@Valid @RequestBody MoveTodoListDto movedLists)  {
        log.info(movedLists.toString());
        todoService.updateTodoLists(movedLists.getTodoLists());
        return ResponseEntity.ok("success");
    }


}
