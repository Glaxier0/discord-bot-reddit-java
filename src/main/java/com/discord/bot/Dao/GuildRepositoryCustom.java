package com.discord.bot.dao;

import com.discord.bot.entity.Guild;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuildRepositoryCustom {
    @Query(value = "SELECT * FROM guilds WHERE guild_id = :guild_id", nativeQuery = true)
    Guild getByGuildId(@Param("guild_id") String guildId);
}
