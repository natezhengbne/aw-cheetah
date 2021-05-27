package com.asyncworking.controllers;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
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
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todolists")
    public ResponseEntity<Long> todoListCreate(@Valid @RequestBody TodoListDto todoListDto) {
        return ResponseEntity.ok(todoService.createTodoList(todoListDto));
    }

    @PostMapping("/projects/{projectid}/todolists/{todolistid}/todoitems")
    public ResponseEntity<Long> createTodoItem(@Valid @RequestBody TodoItemPostDto todoItemPostDto) {
        return ResponseEntity.ok(todoService.createTodoItem(todoItemPostDto));
    }

    @GetMapping("/projects/{projectid}/todolists")
    public ResponseEntity<List<TodoListDto>> requiredNumberTodoLists(@PathVariable Long projectid,
                                                                     @RequestParam("quantity") @NotNull Integer quantity) {
        return ResponseEntity.ok(todoService.findRequiredNumberTodoListsByProjectId(projectid, quantity));
    }

    @GetMapping("/projects/todolists/{todolistid}")
    public ResponseEntity<TodoListDto> todoListFind(@PathVariable Long todolistid) {
        log.info("todolistId:" + todolistid);
        return ResponseEntity.ok(todoService.findTodoListById(todolistid));
    }
}
