package com.discord.bot.service;

import com.discord.bot.entity.Post;

import java.util.List;

@SuppressWarnings("unused")
public interface PostService {
     List<Post> findAll();
     void save(Post post);
     void delete(Post post);
     void deleteAll(List<Post> posts);
     String getByUrl(String url);
     String getByPermaUrl(String permaUrl);
     List<Post> getVideoNullFirebase();
     List<Post> getOldPosts();
     List<Post> getOldFirebaseVideos();
     List<Post> getPosts(String subreddit);
     List<String> getSubredditCount();
     List<Post> getBySubreddits(List<String> subreddits);
     boolean existsByUrlAndPermaUrl(String url, String permaUrl);
}