package com.assignment.todo5.service;

import com.assignment.todo5.client.NotificationServiceClient;
import com.assignment.todo5.dto.TodoDTO;
import com.assignment.todo5.entity.Todo;
import com.assignment.todo5.repository.TodoRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TodoServiceTest {

    @Test
    void testCreateTodo() {

        TodoRepository repo = mock(TodoRepository.class);
        NotificationServiceClient client = mock(NotificationServiceClient.class);

        TodoService service = new TodoService(repo, client);

        TodoDTO dto = new TodoDTO(null, "Task", "Desc", false);
        Todo saved = new Todo(1L, "Task", "Desc", false);

        when(repo.save(any())).thenReturn(saved);

        TodoDTO result = service.createTodo(dto);

        assertEquals("Task", result.getTitle());
        verify(client, times(1)).sendNotification(anyString());
    }

    @Test
    void testGetAllTodos() {

        TodoRepository repo = mock(TodoRepository.class);
        NotificationServiceClient client = mock(NotificationServiceClient.class);

        TodoService service = new TodoService(repo, client);

        when(repo.findAll()).thenReturn(List.of(
                new Todo(1L, "A", "B", false)
        ));

        List<TodoDTO> result = service.getAllTodos();

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllTodosEmpty() {

        TodoRepository repo = mock(TodoRepository.class);
        NotificationServiceClient client = mock(NotificationServiceClient.class);

        TodoService service = new TodoService(repo, client);

        when(repo.findAll()).thenReturn(List.of());

        List<TodoDTO> result = service.getAllTodos();

        assertTrue(result.isEmpty());
    }
}