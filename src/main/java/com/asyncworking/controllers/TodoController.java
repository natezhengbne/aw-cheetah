package com.asyncworking.controllers;


import com.asyncworking.dtos.TodoBoardDto;
import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.services.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/project/todoBoard")
    public ResponseEntity<Long> todoBoardCreate(@Valid @RequestBody TodoBoardDto todoBoardDto) {
        return ResponseEntity.ok(todoService.createTodoBoard(todoBoardDto));
    }

    @PostMapping("/project/todoBoard/todoList")
    public ResponseEntity<Long> todoListCreate(@Valid @RequestBody TodoListDto todoListDto) {
        return ResponseEntity.ok(todoService.createTodoList(todoListDto));
    }

    @GetMapping("/project/todoBoard/todoLists/{projectId}")
    public ResponseEntity<List<TodoListDto>> allTodoLists(@PathVariable Long projectId) {
        log.info("ProjectId: " + projectId);
        return ResponseEntity.ok(todoService.findTodoListsByProjectId(projectId));
    }

}
