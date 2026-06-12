package com.assignment.todo4.controller;
import com.assignment.todo4.dto.TodoDTO;
import com.assignment.todo4.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @PostMapping
    public TodoDTO create(@RequestBody @Valid TodoDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<TodoDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TodoDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public TodoDTO update(@PathVariable Long id, @RequestBody @Valid TodoDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted successfully";
    }
}
