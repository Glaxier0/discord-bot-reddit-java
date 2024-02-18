package com.discord.bot.service;

import com.discord.bot.entity.Post;
import com.discord.bot.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@SuppressWarnings("unused")
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
    public void deleteAll(List<Post> posts) {
        repository.deleteAll(posts);
    }

    @Override
    public String getByUrl(String url) {
        return repository.getPostByUrl(url);
    }

    @Override
    public String getByPermaUrl(String permaUrl) {
        return repository.getPostByPermaUrl(permaUrl);
    }

    @Override
    public List<Post> getVideoNullFirebase() {
        return repository.getPostsByContentTypeAndFirebaseUrlIsNull("video");
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
        return repository.getPostsByContentTypeIsNotNullAndSubredditEqualsIgnoreCase(subreddit);
    }

    @Override
    public List<String> getSubredditCount() {
        return repository.getSubredditCount();
    }

    public List<Post> getBySubreddits(List<String> subreddits) {
        return repository.getPostsBySubredditIn(subreddits);
    }

    @Override
    public boolean existsByUrlAndPermaUrl(String url, String permaUrl) {
        return repository.existsByUrlOrPermaUrl(url, permaUrl);
    }
}
