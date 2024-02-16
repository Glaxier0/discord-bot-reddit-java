package com.discord.bot.repository;

import com.discord.bot.entity.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Integer> {
    Subreddit getSubredditByName(String name);
    List<String> getSubredditNamesByGenre(String genre);
}