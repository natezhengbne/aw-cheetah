package com.asyncworking.controllers;


import com.asyncworking.dtos.TodoBoardDto;
import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.services.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/{projectId}/todo-board")
    public ResponseEntity<Long> todoBoardCreate(@Valid @PathVariable("projectId") Long projectId, @RequestBody TodoBoardDto todoBoardDto) {
        return ResponseEntity.ok(todoService.createTodoBoard(todoBoardDto));
    }

    @PostMapping("/{projectId}/todo-list")
    public ResponseEntity<Long> todoListCreate(@Valid @PathVariable("projectId") Long projectId, @RequestBody TodoListDto todoListDto) {
        return ResponseEntity.ok(todoService.createTodoList(todoListDto));
    }

    @GetMapping("/{projectId}/todo-lists")
    public ResponseEntity<List<TodoListDto>> allTodoLists(@PathVariable("projectId") Long projectId) {
        log.info("ProjectId: " + projectId);
        return ResponseEntity.ok(todoService.findTodoListsByProjectId(projectId));
    }
}
