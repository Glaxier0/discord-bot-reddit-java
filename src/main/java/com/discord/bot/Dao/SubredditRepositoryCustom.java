package com.discord.bot.dao;

import com.discord.bot.entity.Subreddit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubredditRepositoryCustom {
    @Query(value = "SELECT * FROM subreddits WHERE name = :name", nativeQuery = true)
    Subreddit getSubreddit(@Param("name") String name);

    @Query(value = "SELECT name FROM subreddits WHERE genre = :genre", nativeQuery = true)
    List<String> getSubredditsByGenre(@Param("genre") String genre);
}
