package com.discord.bot.Service;

import com.discord.bot.Entity.Post;
import com.discord.bot.Dao.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    PostRepository repository;

    public PostServiceImpl(PostRepository repository) {
        this.repository = repository;
    }

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
    public List<Post> getVideoNullVimeo() {
        return repository.getVideoNullVimeo();
    }

    @Override
    public List<Post> getPosts(String subreddit) {
        return repository.getPosts(subreddit);
    }

    @Override
    public List<String> getSubredditCount() {
        return repository.getSubredditCount();
    }
}
