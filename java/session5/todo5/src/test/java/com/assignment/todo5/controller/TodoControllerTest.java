package com.assignment.todo5.controller;

import com.assignment.todo5.dto.TodoDTO;
import com.assignment.todo5.service.TodoService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TodoControllerTest {

    @Test
    void testCreate() {

        TodoService service = mock(TodoService.class);

        TodoController controller = new TodoController(service);

        TodoDTO dto = new TodoDTO(1L, "Task", "Desc", false);

        when(service.createTodo(any())).thenReturn(dto);

        TodoDTO result = controller.create(dto);

        assertEquals("Task", result.getTitle());
    }

    @Test
    void testGetAll() {

        TodoService service = mock(TodoService.class);

        TodoController controller = new TodoController(service);

        when(service.getAllTodos()).thenReturn(List.of());

        List<TodoDTO> result = controller.getAll();

        assertNotNull(result);
    }
}