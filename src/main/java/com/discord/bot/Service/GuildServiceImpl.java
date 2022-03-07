package com.discord.bot.service;

import com.discord.bot.dao.GuildRepository;
import com.discord.bot.entity.Guild;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GuildServiceImpl implements GuildService{
    GuildRepository guildRepository;

    @Override
    public List<Guild> findAll() {
        return guildRepository.findAll();
    }

    @Override
    public void save(Guild guild) {
        guildRepository.save(guild);
    }

    @Override
    public void delete(Guild guild) {
        guildRepository.delete(guild);
    }

    @Override
    public Guild getByGuildId(String guildId) {
        return guildRepository.getByGuildId(guildId);
    }
}
