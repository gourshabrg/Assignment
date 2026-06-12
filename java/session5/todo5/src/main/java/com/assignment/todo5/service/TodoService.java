package com.assignment.todo5.service;

import com.assignment.todo5.client.NotificationServiceClient;
import com.assignment.todo5.dto.TodoDTO;
import com.assignment.todo5.entity.Todo;
import com.assignment.todo5.mapper.TodoMapper;
import com.assignment.todo5.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository repository;
    private final NotificationServiceClient notificationService;

    public TodoService(TodoRepository repository,
                       NotificationServiceClient notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public TodoDTO createTodo(TodoDTO dto) {
        logger.info("Creating TODO");

        Todo saved = repository.save(TodoMapper.toEntity(dto));

        notificationService.sendNotification("Notification sent for new TODO");

        return TodoMapper.toDTO(saved);
    }

    public List<TodoDTO> getAllTodos() {
        return repository.findAll()
                .stream()
                .map(TodoMapper::toDTO)
                .collect(Collectors.toList());
    }
}