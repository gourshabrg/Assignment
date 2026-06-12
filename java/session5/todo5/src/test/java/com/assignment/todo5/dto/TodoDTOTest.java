package com.assignment.todo5.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TodoDTOTest {

    @Test
    void testDTO() {

        TodoDTO dto = new TodoDTO();

        dto.setId(1L);
        dto.setTitle("Task");
        dto.setDescription("Desc");
        dto.setCompleted(true);

        assertEquals("Task", dto.getTitle());
        assertTrue(dto.isCompleted());
    }
}