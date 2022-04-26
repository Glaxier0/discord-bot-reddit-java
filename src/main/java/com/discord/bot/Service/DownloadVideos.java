package com.discord.bot.service;

import com.discord.bot.entity.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Objects;

@Service
public class DownloadVideos {
    UploadToFirebase uploadToFirebaseService;
    PostService postService;
    @Value("${firebase_storage_bucket_name}")
    private String BUCKET_NAME;

    public DownloadVideos(UploadToFirebase uploadToFirebaseService, PostService postService) {
        this.uploadToFirebaseService = uploadToFirebaseService;
        this.postService = postService;
    }

    public void downloadVideos() {
        System.out.println("Program in download videos");

        List<Post> list = postService.getVideoNullFirebase();
        System.out.println("File count: " + list.size());

        for (Post post : list) {
            URL url = null;
            try {
                url = new URL(post.getDownloadUrl());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                InputStream inputStream = Objects.requireNonNull(url).openStream();
                ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
                FileOutputStream fileOutputStream = new FileOutputStream("temp.mp4");
                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                uploadToFirebaseService.uploadToFirebaseStorage(postService, BUCKET_NAME, post);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Downloading videos is done!");
    }
}
