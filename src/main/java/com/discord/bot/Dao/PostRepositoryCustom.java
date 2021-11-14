package com.discord.bot.Dao;

import com.discord.bot.Entity.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PostRepositoryCustom {

    @Query(value = "SELECT url from posts where url = :url", nativeQuery = true)
    String getByUrl(@Param("url") String url);

    @Query(value = "SELECT perma_url from posts where perma_url = :url", nativeQuery = true)
    String getByPermaUrl(@Param("url") String permaUrl);

    @Query(value = "SELECT * FROM posts WHERE type = 'video' AND firebase_url IS NULL", nativeQuery = true)
    List<Post> getVideoNullFirebase();

    @Query(value = "SELECT * FROM posts WHERE created < NOW() - INTERVAL '4 days' AND type != 'video';"
            , nativeQuery = true)
    List<Post> getOldPosts();

    @Query(value = "SELECT * FROM posts WHERE created < NOW() - INTERVAL '4 days' AND firebase_url IS NOT NULL;"
            , nativeQuery = true)
    List<Post> getOldFirebaseVideos();

    @Query(value = "SELECT * FROM posts WHERE type IS NOT NULL AND subreddit = :subreddit",nativeQuery = true)
    List<Post> getPosts(@Param("subreddit") String subreddit);

    @Query(value = "SELECT * FROM posts WHERE subreddit IN ('hentai', 'HENTAI_GIF', 'rule34')", nativeQuery = true)
    List<Post> getHentai();

    @Query(value = "SELECT * FROM posts WHERE subreddit IN ('porninaminute', 'porninfifteenseconds', 'porn', " +
            "'anal_gifs', 'porn_gifs')", nativeQuery = true)
    List<Post> getPorn();

    @Query(value = "SELECT subreddit, COUNT(subreddit) FROM posts GROUP BY subreddit", nativeQuery = true)
    List<String> getSubredditCount();
}

