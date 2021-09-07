package com.discord.bot.Service;

import com.discord.bot.Dao.TodoRepository;
import com.discord.bot.Entity.Todo;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TodoServiceImpl implements TodoService{
    TodoRepository repository;

    public TodoServiceImpl(TodoRepository repository) {
        this.repository = repository;
    }

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
}
