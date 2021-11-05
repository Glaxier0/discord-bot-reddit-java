package com.discord.bot.Dao;

import com.discord.bot.Entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepositoryCustom {

    @Query(value = "SELECT * FROM users WHERE user_id = :username", nativeQuery = true)
    User getUser(@Param("username") String username);

    @Query(value = "SELECT * FROM users", nativeQuery = true)
    List<User> getUsers();
}
