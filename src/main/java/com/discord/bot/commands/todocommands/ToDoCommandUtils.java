package com.discord.bot.commands.todocommands;

import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ToDoCommandUtils {
    UserService userService;

    public void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag);
        }
        user.setTodoCount(user.getTodoCount() + 1);
        userService.save(user);
    }
}
