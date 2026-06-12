package com.assignment.todo5.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TodoEntityTest {

    @Test
    void testEntity() {

        Todo todo = new Todo();

        todo.setId(1L);
        todo.setTitle("Task");
        todo.setCompleted(true);

        assertEquals("Task", todo.getTitle());
    }
}