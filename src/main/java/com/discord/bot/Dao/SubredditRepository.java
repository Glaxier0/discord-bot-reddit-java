package com.discord.bot.dao;

import com.discord.bot.entity.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Integer>, SubredditRepositoryCustom {
}