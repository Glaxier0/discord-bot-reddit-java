package com.discord.bot.service;

import com.discord.bot.entity.Post;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class RemoveOldPosts {
    PostService postService;

    public RemoveOldPosts(PostService postService) {
        this.postService = postService;
    }

    /**
     * Removes old posts from database
     */
    public void removeOldPosts() {
        System.out.println("Program in remove old posts.");

        List<Post> posts = postService.getOldPosts();
        System.out.println("Post count to be deleted: " + posts.size());

        for (Post post : posts) {
            postService.delete(post);
        }
        System.out.println("Deleting old posts done!");
    }
}
