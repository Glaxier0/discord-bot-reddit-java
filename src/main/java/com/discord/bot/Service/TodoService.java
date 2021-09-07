package com.discord.bot.Service;

import com.discord.bot.Entity.Todo;
import java.util.List;

public interface TodoService {
    List<Todo> findAll();
    void save(Todo todo);
    void delete(Todo todo);
    List<Todo> todoList(String discordUser);
}
