package com.discord.bot.commands.musiccommands;

import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;
import net.dv8tion.jda.api.entities.GuildVoiceState;

public class MusicCommandUtils {
    UserService userService;

    public MusicCommandUtils(UserService userService) {
        this.userService = userService;
    }

    public void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag, 0);
        }
        user.setMusicCount(user.getMusicCount() + 1);
        userService.save(user);
    }

    public boolean channelControl(GuildVoiceState selfVoiceState, GuildVoiceState memberVoiceState) {
        if (!selfVoiceState.inAudioChannel()) {
            return false;
        }
        if (!memberVoiceState.inAudioChannel()) {
            return false;
        }
        return memberVoiceState.getChannel() == selfVoiceState.getChannel();
    }
}
