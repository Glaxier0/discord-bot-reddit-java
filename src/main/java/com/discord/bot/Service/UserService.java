package com.discord.bot.service;

import com.discord.bot.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    void save(User user);

    void delete(User user);

    User getUser(String username);

    List<User> getUsers();
}
