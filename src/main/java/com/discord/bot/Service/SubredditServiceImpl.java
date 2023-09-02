package com.discord.bot.service;

import com.discord.bot.dao.SubredditRepository;
import com.discord.bot.entity.Subreddit;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SubredditServiceImpl implements SubredditService {
    SubredditRepository repository;

    @Override
    public List<Subreddit> findAll() {
        return repository.findAll();
    }

    @Override
    public void save(Subreddit subreddit) {
        repository.save(subreddit);
    }

    @Override
    public void delete(Subreddit subreddit) {
        repository.delete(subreddit);
    }

    @Override
    public Subreddit getSubreddit(String name) {
        return repository.getSubreddit(name);
    }

    @Override
    public List<Subreddit> getSubreddits() {
        return repository.findAll();
    }

    public List<String> getSubredditsByGenre(String genre) {
        return repository.getSubredditsByGenre(genre);
    }
}