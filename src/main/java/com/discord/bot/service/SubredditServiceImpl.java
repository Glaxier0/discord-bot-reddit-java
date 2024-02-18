package com.discord.bot.service;

import com.discord.bot.repository.SubredditRepository;
import com.discord.bot.entity.Subreddit;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings("unused")
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
        return repository.getSubredditByName(name);
    }

    @Override
    public List<Subreddit> getSubreddits() {
        return repository.findAll();
    }

    @Override
    public List<String> getSubredditsByGenre(String genre) {
        return repository.getSubredditNamesByGenre(genre);
    }
}