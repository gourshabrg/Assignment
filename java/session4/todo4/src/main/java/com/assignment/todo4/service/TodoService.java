package com.assignment.todo4.service;
import com.assignment.todo4.dto.TodoDTO;
import java.util.List;

public interface TodoService {
    TodoDTO create(TodoDTO dto);
    List<TodoDTO> getAll();
    TodoDTO getById(Long id);
    TodoDTO update(Long id, TodoDTO dto);
    void delete(Long id);
}