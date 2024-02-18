package com.discord.bot.service;

import com.discord.bot.entity.Post;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RemoveOldPosts {
    private static final Logger logger = LoggerFactory.getLogger(RemoveOldPosts.class);
    PostService postService;

    public void removeOldPosts() {
        logger.info("Program in remove old posts.");

        List<Post> posts = postService.getOldPosts();
        logger.info("Post count to be deleted: " + posts.size());

        postService.deleteAll(posts);
        logger.info("Deleting old posts is done!");
    }
}
