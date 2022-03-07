package com.discord.bot.service;

import com.discord.bot.dao.PostRepository;
import com.discord.bot.entity.Post;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    PostRepository repository;

    @Override
    public List<Post> findAll() {
        return repository.findAll();
    }

    @Override
    public void save(Post post) {
        repository.save(post);
    }

    @Override
    public void delete(Post post) {
        repository.delete(post);
    }

    @Override
    public String getByUrl(String url) {
        return repository.getByUrl(url);
    }

    @Override
    public String getByPermaUrl(String permaUrl) {
        return repository.getByPermaUrl(permaUrl);
    }

    @Override
    public List<Post> getVideoNullFirebase() {
        return repository.getVideoNullFirebase();
    }

    @Override
    public List<Post> getOldPosts() {
        return repository.getOldPosts();
    }

    @Override
    public List<Post> getOldFirebaseVideos() {
        return repository.getOldFirebaseVideos();
    }

    @Override
    public List<Post> getPosts(String subreddit) {
        return repository.getPosts(subreddit);
    }

    @Override
    public List<String> getSubredditCount() {
        return repository.getSubredditCount();
    }

    @Override
    public List<Post> getHentai() {
        return repository.getHentai();
    }

    @Override
    public List<Post> getPorn() {
        return repository.getPorn();
    }
}
