package com.discord.bot.service;

import com.discord.bot.entity.Post;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class FirebaseServiceImpl implements FirebaseService {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseServiceImpl.class);
    final PostService postService;

    @Value("${firebase.adminsdk.file.name}")
    private String FILE_NAME;

    @Value("${firebase.storage.bucket.name}")
    String BUCKET_NAME;

    public FirebaseServiceImpl(PostService postService) {
        this.postService = postService;
    }

    public void downloadVideos() {
        logger.info("Program in download videos");

        List<Post> list = postService.getVideoNullFirebase();
        logger.info("File count: " + list.size());

        for (Post post : list) {
            URL url = createUri(post.getDownloadUrl());

            try (InputStream inputStream = Objects.requireNonNull(url).openStream();
                 ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
                 FileOutputStream fileOutputStream = new FileOutputStream("temp.mp4")) {
                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                uploadToFirebaseStorage(post);
            } catch (IOException e) {
                logger.error("Error occurred while downloading and uploading file", e);
            }
        }
        logger.info("Downloading videos is done!");
    }

    public void removeOldFirebaseVideos() {
        logger.info("Program in remove old firebase videos.");

        FileInputStream serviceAccount;
        Storage storage = null;
        try {
            serviceAccount = new FileInputStream(FILE_NAME);
            storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials
                    .fromStream(serviceAccount)).build().getService();
        } catch (IOException e) {
            logger.error("Error occurred while removing old firebase videos", e);
        }

        List<Post> posts = postService.getOldFirebaseVideos();
        logger.info("Post count to be deleted: " + posts.size());

        for (Post post : posts) {
            String blobName = post.getId() + ".mp4";
            BlobId blobId = BlobId.of(BUCKET_NAME, blobName);
            boolean deleted = Objects.requireNonNull(storage).delete(blobId);
            if (deleted) {
                postService.delete(post);
            }
        }
        logger.info("Deleting old firebase videos is done!");
    }

    private void uploadToFirebaseStorage(Post post) throws IOException {
        FileInputStream serviceAccount = new FileInputStream(FILE_NAME);
        String fileName = String.valueOf(post.getId());
        Map<String, String> map = new HashMap<>();
        map.put("firebaseStorageDownloadTokens", fileName);
        InputStream file = new FileInputStream("temp.mp4");
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName + ".mp4");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setMetadata(map).setContentType("video/mp4").build();
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials
                .fromStream(serviceAccount)).build().getService();

        try {
            Blob blob = storage.createFrom(blobInfo, file);
            String firebaseUrl = "https://firebasestorage.googleapis.com/v0/b/" + BUCKET_NAME + "/o/"
                    + fileName + ".mp4" + "?alt=media&token=" + Objects.requireNonNull(blob.getMetadata()).get("firebaseStorageDownloadTokens");
            post.setFirebaseUrl(firebaseUrl);
            postService.save(post);
        } catch (Exception e) {
            logger.error("Error occurred while uploading videos to firebase", e);
        }
    }

    private URL createUri(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }
}