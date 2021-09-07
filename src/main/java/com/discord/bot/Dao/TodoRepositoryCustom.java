package com.discord.bot.Dao;

import com.discord.bot.Entity.Todo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TodoRepositoryCustom {

    @Query(value = "SELECT * FROM todo WHERE discord_user = :user", nativeQuery = true)
    List<Todo> todoList(@Param("user") String discordUser);
}
