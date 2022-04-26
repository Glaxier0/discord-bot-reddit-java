package com.discord.bot.service;

import com.discord.bot.entity.Post;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class UploadToFirebase {
    @Value("${firebase_adminsdk_file_name}")
    private String FILE_NAME;

    public void uploadToFirebaseStorage(PostService postService, String BUCKET_NAME, Post post) throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream(FILE_NAME);
        String fileName = String.valueOf(post.getId());
        Map<String, String> map = new HashMap<>();
        map.put("firebaseStorageDownloadTokens", fileName);
        InputStream file = new FileInputStream("temp.mp4");
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName + ".mp4");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setMetadata(map).setContentType("video/mp4").build();
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials
                .fromStream(serviceAccount)).build().getService();

        try {
            Blob blob = storage.create(blobInfo, file);
            String firebaseUrl = "https://firebasestorage.googleapis.com/v0/b/" + BUCKET_NAME + "/o/"
                    + fileName + ".mp4" + "?alt=media&token=" + blob.getMetadata().get("firebaseStorageDownloadTokens");
            post.setFirebaseUrl(firebaseUrl);
            postService.save(post);
        } catch (Exception e) {
            System.out.println("File upload failed");
            e.printStackTrace();
        }
    }
}
