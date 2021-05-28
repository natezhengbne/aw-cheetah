package com.asyncworking.controllers;

import com.asyncworking.dtos.TodoListDto;
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
@RequestMapping("/projects/{projectid}/todolists")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<Long> createTodoList(@Valid @RequestBody TodoListDto todoListDto) {
        return ResponseEntity.ok(todoService.createTodoList(todoListDto));
    }

    @PostMapping("/{todolistid}/todoitems")
    public ResponseEntity createTodoItem(@Valid @RequestBody TodoItemPostDto todoItemPostDto) {
        todoService.createTodoItem(todoItemPostDto);
        return ResponseEntity.ok("create todo item success");
    }

    @GetMapping
    public ResponseEntity<List<TodoListDto>> fetchTodoLists(@PathVariable Long projectid,
                                                                     @RequestParam("quantity") @NotNull Integer quantity) {
        return ResponseEntity.ok(todoService.findRequiredNumberTodoListsByProjectId(projectid, quantity));
    }

    @GetMapping("/{todolistid}")
    public ResponseEntity<TodoListDto> fetchSingleTodoList(@PathVariable Long todolistid) {
        log.info("todolistId:" + todolistid);
        return ResponseEntity.ok(todoService.findTodoListById(todolistid));
    }
}
