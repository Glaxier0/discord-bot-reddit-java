package com.discord.bot.dao;

import com.discord.bot.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepositoryCustom {

    @Query(value = "SELECT * FROM users WHERE user_id = :username", nativeQuery = true)
    User getUser(@Param("username") String username);

    @Query(value = "SELECT * FROM users ORDER BY (text_count + h_count + p_count + reddit_count + todo_count + music_count) DESC", nativeQuery = true)
    List<User> getUsers();
}
