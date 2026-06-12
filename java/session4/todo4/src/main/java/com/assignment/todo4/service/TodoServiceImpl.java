package com.assignment.todo4.service;
import com.assignment.todo4.dto.TodoDTO;
import com.assignment.todo4.entity.Todo;
import com.assignment.todo4.mapper.TodoMapper;
import com.assignment.todo4.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository repository;

    public TodoServiceImpl(TodoRepository repository) {
        this.repository = repository;
    }

    @Override
    public TodoDTO create(TodoDTO dto) {
        Todo todo = TodoMapper.toEntity(dto);
        return TodoMapper.toDTO(repository.save(todo));
    }

    @Override
    public List<TodoDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(TodoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TodoDTO getById(Long id) {
        Todo todo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));
        return TodoMapper.toDTO(todo);
    }

    @Override
    public TodoDTO update(Long id, TodoDTO dto) {
        Todo todo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());

        Todo.Status newStatus = Todo.Status.valueOf(dto.getStatus());

        if ((todo.getStatus() == Todo.Status.PENDING && newStatus == Todo.Status.COMPLETED) ||
            (todo.getStatus() == Todo.Status.COMPLETED && newStatus == Todo.Status.PENDING)) {
            todo.setStatus(newStatus);
        } else {
            throw new RuntimeException("Invalid status transition");
        }

        return TodoMapper.toDTO(repository.save(todo));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}