package com.discord.bot.repository;

import com.discord.bot.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> getTodosByDiscordUser(String discordUser);
    @Transactional
    void deleteTodosByDiscordUser(String discordUser);
}