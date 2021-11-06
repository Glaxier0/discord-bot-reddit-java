package com.discord.bot.Service;

import com.discord.bot.Entity.Post;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class DownloadVideos {
    UploadToFirebase uploadToFirebaseService;

    public void downloadVideos(PostService postService, String BUCKET_NAME) {
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
