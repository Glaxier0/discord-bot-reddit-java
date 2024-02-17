package com.discord.bot.Service;

import com.discord.bot.Entity.User;

import java.util.List;

public interface UserService {
    @SuppressWarnings("unused")
    List<User> findAll();
    void save(User user);
    void delete(User user);
    User getUser(String username);
    List<User> getUsers();
}
