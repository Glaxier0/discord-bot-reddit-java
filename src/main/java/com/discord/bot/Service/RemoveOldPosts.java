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

@Service
public class RemoveOldPosts {
    PostService postService;

    @Value("${firebase_storage_bucket_name}")
    String BUCKET_NAME;
    @Value("${firebase_adminsdk_file_name}")
    private String FILE_NAME;

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

    public void removeOldFirebaseVideos() {
        System.out.println("Program in remove old firebase videos.");

        FileInputStream serviceAccount;
        Storage storage = null;
        try {
            serviceAccount = new FileInputStream(FILE_NAME);
            storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials
                    .fromStream(serviceAccount)).build().getService();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Post> posts = postService.getOldFirebaseVideos();
        System.out.println("Post count to be deleted: " + posts.size());

        for (Post post : posts) {
            String blobName = post.getId() + ".mp4";
            BlobId blobId = BlobId.of(BUCKET_NAME, blobName);
            boolean deleted = storage.delete(blobId);
            if (deleted) {
                postService.delete(post);
            }
        }
        System.out.println("Deleting old firebase videos done!");
    }
}
