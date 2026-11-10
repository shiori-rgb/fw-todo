package com.example.todoapp;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private final TodoMapper todoMapper;

    public TodoService(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    public List<Todo> search(String keyword, String category, String order) {
        return todoMapper.search(keyword, category, order);
    }

    public Todo findById(Long id) {
        return todoMapper.findById(id);
    }

    public int create(Todo todo) {
        return todoMapper.insert(todo);
    }

    public int update(Todo todo) {
        return todoMapper.update(todo);
    }

    public int delete(Long id) {
        return todoMapper.deleteById(id);
    }
}
