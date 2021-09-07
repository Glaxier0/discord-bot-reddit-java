package com.discord.bot.Service;

import com.discord.bot.Entity.Post;
import java.util.List;

public interface PostService {
     List<Post> findAll();
     void save(Post post);
     void delete(Post post);
     String getByUrl(String url);
     List<Post> getVideoNullVimeo ();
     List<Post> getPosts(String subreddit);
     List<String> getSubredditCount();
     List<Post> getHentai();
     List<Post> getPorn();
}
