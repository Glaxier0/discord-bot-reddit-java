package com.discord.bot.commands.textcommands;

import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;

public class TextCommandUtils {
    UserService userService;

    public TextCommandUtils(UserService userService) {
        this.userService = userService;
    }

    public void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag);
        }
        user.setTextCount(user.getTextCount() + 1);
        userService.save(user);
    }
}
