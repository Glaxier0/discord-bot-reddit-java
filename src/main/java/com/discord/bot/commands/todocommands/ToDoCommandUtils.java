package com.discord.bot.commands.todocommands;

import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;

public class ToDoCommandUtils {
    UserService userService;

    public ToDoCommandUtils(UserService userService) {
        this.userService = userService;
    }

    public void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag);
        }
        user.setTodoCount(user.getTodoCount() + 1);
        userService.save(user);
    }
}
