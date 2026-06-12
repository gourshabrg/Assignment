package com.assignment.todo5.mapper;

import com.assignment.todo5.dto.TodoDTO;
import com.assignment.todo5.entity.Todo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TodoMapperTest {

    @Test
    void testToDTO() {
        Todo todo = new Todo(1L, "Task", "Desc", false);
        TodoDTO dto = TodoMapper.toDTO(todo);

        assertEquals("Task", dto.getTitle());
    }

    @Test
    void testToEntity() {
        TodoDTO dto = new TodoDTO(1L, "Task", "Desc", true);
        Todo todo = TodoMapper.toEntity(dto);

        assertTrue(todo.isCompleted());
    }
}