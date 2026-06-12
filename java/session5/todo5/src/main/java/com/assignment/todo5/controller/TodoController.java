package com.assignment.todo5.controller;

import com.assignment.todo5.dto.TodoDTO;
import com.assignment.todo5.service.TodoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @PostMapping
    public TodoDTO create(@Valid @RequestBody TodoDTO dto) {
        logger.info("POST /api/todos called");
        return service.createTodo(dto);
    }

    @GetMapping
    public List<TodoDTO> getAll() {
        logger.info("GET /api/todos called");
        return service.getAllTodos();
    }
}