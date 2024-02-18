package com.discord.bot.repository;

import com.discord.bot.entity.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Integer> {
    Subreddit getSubredditByName(String name);
    @Query(value = "SELECT s.name FROM Subreddit s WHERE s.genre = :genre")
    List<String> getSubredditNamesByGenre(String genre);
}