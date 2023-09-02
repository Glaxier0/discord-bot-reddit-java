package com.discord.bot.dao;

import com.discord.bot.entity.Todo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TodoRepositoryCustom {
    @Query(value = "SELECT * FROM todo WHERE discord_user = :user", nativeQuery = true)
    List<Todo> todoList(@Param("user") String discordUser);

    @Query(value = "DELETE FROM todo WHERE discord_user = :user", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteAll(@Param("user") String discordUser);
}
