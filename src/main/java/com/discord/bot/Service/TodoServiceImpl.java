package com.discord.bot.service;

import com.discord.bot.dao.TodoRepository;
import com.discord.bot.entity.Todo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TodoServiceImpl implements TodoService{

    TodoRepository repository;

    @Override
    public List<Todo> findAll() {
        return repository.findAll();
    }

    @Override
    public void save(Todo todo) {
        repository.save(todo);
    }

    @Override
    public void delete(Todo todo) {
        repository.delete(todo);
    }

    @Override
    public List<Todo> todoList(String discordUser) {
        return repository.todoList(discordUser);
    }

    @Override
    public void deleteAll(String discordUser) {
        repository.deleteAll(discordUser);
    }
}
