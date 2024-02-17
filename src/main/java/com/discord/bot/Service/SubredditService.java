package com.discord.bot.Service;

import com.discord.bot.Entity.Subreddit;

import java.util.List;
public interface SubredditService {
    List<Subreddit> findAll();
    void save(Subreddit subreddit);
    void delete(Subreddit subreddit);
    Subreddit getSubreddit(String name);
    List<Subreddit> getSubreddits();
    List<String> getSubredditsByGenre(String genre);
}
