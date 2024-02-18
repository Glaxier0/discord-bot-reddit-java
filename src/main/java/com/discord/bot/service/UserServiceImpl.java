package com.discord.bot.service;

import com.discord.bot.repository.UserRepository;
import com.discord.bot.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@SuppressWarnings("unused")
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    UserRepository repository;

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public void save(User user) {
        repository.save(user);
    }

    @Override
    public void delete(User user) {
        repository.delete(user);
    }

    @Override
    public User getUser(String userId) {
        return repository.getUserByUserId(userId);
    }

    @Override
    public List<User> getUsers() {
        return repository.getUsers();
    }
}
