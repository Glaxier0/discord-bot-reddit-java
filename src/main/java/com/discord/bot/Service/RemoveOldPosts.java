package com.discord.bot.Service;

import com.discord.bot.Entity.Post;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class RemoveOldPosts {

    /**
     * Removes old posts from database
     */
    public void removeOldPosts(PostService postService) {
        System.out.println("Program in remove old posts.");

        List<Post> posts = postService.getOldPosts();
        System.out.println("Post count to be deleted: " + posts.size());

        for (Post post : posts) {
            postService.delete(post);
        }
        System.out.println("Deleting old posts done!");
    }

    public void removeOldFirebaseVideos(PostService postService, String BUCKET_NAME) throws IOException {
        System.out.println("Program in remove old firebase videos.");

        FileInputStream serviceAccount =
                new FileInputStream("r3dd-1-firebase-adminsdk-h0reo-019535d8b2.json");
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials
                .fromStream(serviceAccount)).build().getService();

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
