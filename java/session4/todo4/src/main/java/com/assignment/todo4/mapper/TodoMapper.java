package com.assignment.todo4.mapper;


import com.assignment.todo4.dto.TodoDTO;
import com.assignment.todo4.entity.Todo;

import java.time.LocalDateTime;

public class TodoMapper {

    public static Todo toEntity(TodoDTO dto) {
        Todo.Status status = dto.getStatus() == null
                ? Todo.Status.PENDING
                : Todo.Status.valueOf(dto.getStatus());

        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setStatus(status);
        todo.setCreatedAt(LocalDateTime.now());

        return todo;
    }

    public static TodoDTO toDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setStatus(todo.getStatus().name());
        return dto;
    }
}