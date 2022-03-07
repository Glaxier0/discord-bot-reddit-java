package com.discord.bot.service;

import com.discord.bot.entity.Todo;

import java.util.List;

public interface TodoService {
    List<Todo> findAll();

    void save(Todo todo);

    void delete(Todo todo);

    List<Todo> todoList(String discordUser);

    void deleteAll(String discordUser);
}
