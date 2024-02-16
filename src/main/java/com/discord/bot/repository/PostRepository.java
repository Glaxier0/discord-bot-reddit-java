package com.discord.bot.repository;

import com.discord.bot.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    String getPostByUrl(String url);

    String getPostByPermaUrl(String permaUrl);

    List<Post> getPostsByContentTypeAndFirebaseUrlIsNull(String Type);

    List<Post> getPostsByContentTypeIsNotNullAndSubredditEqualsIgnoreCase(String subreddit);

    List<Post> getPostsBySubredditIn(List<String> subreddits);

    @Query(value = "SELECT * FROM posts WHERE created < NOW() - INTERVAL '4 days' AND type != 'video';"
            , nativeQuery = true)
    List<Post> getOldPosts();

    @Query(value = "SELECT * FROM posts WHERE created < NOW() - INTERVAL '4 days' AND firebase_url IS NOT NULL;"
            , nativeQuery = true)
    List<Post> getOldFirebaseVideos();

    @Query(value = "SELECT subreddit, COUNT(subreddit) FROM posts GROUP BY subreddit", nativeQuery = true)
    List<String> getSubredditCount();
}