package com.assignment.todo5.mapper;
import com.assignment.todo5.dto.TodoDTO;
import com.assignment.todo5.entity.Todo;

public class TodoMapper {

    public static TodoDTO toDTO(Todo todo) {
        return new TodoDTO(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted()
        );
    }

    public static Todo toEntity(TodoDTO dto) {
        return new Todo(
                dto.getId(),
                dto.getTitle(),
                dto.getDescription(),
                dto.isCompleted()
        );
    }
}