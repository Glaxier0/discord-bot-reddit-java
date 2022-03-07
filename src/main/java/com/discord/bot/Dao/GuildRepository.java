package com.discord.bot.dao;

import com.discord.bot.entity.Guild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuildRepository extends JpaRepository<Guild, Integer>, GuildRepositoryCustom {
}
