package com.discord.bot.service;

import com.discord.bot.entity.Guild;

import java.util.List;

public interface GuildService {
    List<Guild> findAll();
    void save(Guild guild);
    void delete(Guild guild);
    Guild getByGuildId(String guildId);
}
