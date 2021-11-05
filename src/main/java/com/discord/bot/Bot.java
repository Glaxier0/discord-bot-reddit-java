package com.discord.bot;

import com.discord.bot.Entity.Post;
import com.discord.bot.Event.*;
import com.discord.bot.Service.PostService;
import com.discord.bot.Service.TodoService;
import com.discord.bot.Service.UserService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;


@Configuration
@EnableScheduling
public class Bot {

    PostService postService;
    TodoService todoService;
    UserService userService;
    @Value("${reddit_username}")
    private String REDDIT_USERNAME;
    @Value("${reddit_password}")
    private String REDDIT_PASSWORD;
    @Value("${reddit_client_id}")
    private String REDDIT_CLIENT_ID;
    @Value("${reddit_client_secret}")
    private String REDDIT_CLIENT_SECRET;
    @Value("${discord_bot_token}")
    private String DISCORD_TOKEN;

    public Bot(PostService postService, TodoService todoService, UserService userService) {
        this.postService = postService;
        this.todoService = todoService;
        this.userService = userService;
    }


    @Bean
    public void startDiscordBot() {
        try {
            JDA jda = JDABuilder.createDefault(DISCORD_TOKEN).build();
            jda.getPresence().setActivity(Activity.playing("Type !help"));
            jda.addEventListener(new RedditCommands(postService, userService), new TextCommands(userService),
                    new AdminCommands(postService, userService),
                    new NsfwCommands(postService, userService), new ToDoCommands(todoService, userService));

            System.out.println("Starting bot is done!");
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 3600000)
    public void hourDelay() {
        searchReddit();
        downloadVideos();
    }

    @Scheduled(fixedDelay = 86400000)
    public void dayDelay() throws IOException {
        removeOldPosts();
        removeOldFirebaseVideos();
    }

    /**
     * Searchs specialized subreddits and save them to database
     */
    private void searchReddit() {
        System.out.println("Program in search reddit.");

        UserAgent userAgent = new UserAgent("Chrome", "com.discord.bot",
                "v1.0", REDDIT_USERNAME);
        NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
        RedditClient redditClient = OAuthHelper.automatic(networkAdapter,
                Credentials.script(REDDIT_USERNAME, REDDIT_PASSWORD,
                        REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET));

        //Embedded link need to be embedable in discord to nsfw videos work. Like redgifs.com or gyfcat.
        //Keep in mind these things adding more nsfw subreddits to list.
        List<String> subreddits = Arrays.asList("Unexpected","memes", "dankmemes", "greentext",
                "hentai", "HENTAI_GIF", "rule34", "porninaminute",
                "porninfifteenseconds", "porn", "anal_gifs", "porn_gifs");

        List<DefaultPaginator<Submission>> paginatorList = new ArrayList<>();

        for (String s : subreddits) {
            paginatorList.add(redditClient.subreddit(s)
                    .posts()
                    .sorting(SubredditSort.TOP)
                    .timePeriod(TimePeriod.DAY)
                    .build());
        }

        for (DefaultPaginator<Submission> d : paginatorList) {
            Listing<Submission> submissions = d.next();
            for (Submission s : submissions) {

                Post post = new Post(s.getUrl(), s.getSubreddit(), s.getTitle(), s.getAuthor(), s.getCreated());
                post.setPermaUrl("https://reddit.com" + s.getPermalink());

                if (!s.isNsfw()) {
                    if (s.getUrl().contains("https://v.redd.it") && Objects.requireNonNull(Objects.requireNonNull
                            (s.getEmbeddedMedia()).getRedditVideo()).getDuration() <= 60) {
                        post.setContentType("video");
                        //Can be done in 1 variable but this way string is easier to edit, and it is easier to read.
                        String fallbackUrl = Objects.requireNonNull(Objects.requireNonNull(s.getEmbeddedMedia())
                                .getRedditVideo()).getFallbackUrl();
                        String fallbackVideo = fallbackUrl.substring(0, fallbackUrl.indexOf("?"));
                        String fallbackAudio = fallbackVideo.substring(0, fallbackVideo.indexOf("_") + 1) + "audio.mp4";
                        String baseDownloadUrl = "https://ds.redditsave.com/download.php?permalink=https://reddit.com";
                        String videoDownloadUrl = baseDownloadUrl + s.getPermalink() + "&video_url=";
                        String fallbackVideoDownloadUrl = videoDownloadUrl + fallbackUrl + "&audio_url=";
                        String fallbackVideoWithAudioDownloadUrl = fallbackVideoDownloadUrl
                                + fallbackAudio + "?source=fallback";
                        post.setDownloadUrl(fallbackVideoWithAudioDownloadUrl);
                    } else if (s.getUrl().contains(".gif") || s.getUrl().contains("gfycat.com")) {
                        post.setContentType("gif");
                    } else if (s.getUrl().contains(".jpg") || s.getUrl().contains(".png")) {
                        post.setContentType("image");
                    } else if (s.getUrl().contains("https://www.reddit.com/")) {
                        post.setContentType("text");
                    }
                } else {
                    if (s.getUrl().contains("https://www.reddit.com/gallery/")) {
                        post.setContentType("text");
                    } else {
                        post.setContentType("image");
                    }
                }

                String queryPost = postService.getByUrl(post.getUrl());

                if (!post.getUrl().equals(queryPost)) {
                    postService.save(post);
                    System.out.println("URL saved to database: " + post.getUrl());
                }
            }
            try {//30 sec wait because of reddit timeout if too many request occurs.
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Reddit search done!");
    }

    private void downloadVideos() {
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
                uploadToFirebaseStorage(post);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Downloading videos is done!");
    }

    private void uploadToFirebaseStorage(Post post) throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("{path/to/firebasestorage/adminsdk.json}");
        String fileName = String.valueOf(post.getId());
        String bucketName = "Your bucket name including .appspot.com";
        Map<String, String> map = new HashMap<>();
        map.put("firebaseStorageDownloadTokens", fileName);
        InputStream file = new FileInputStream("temp.mp4");
        BlobId blobId = BlobId.of(bucketName, fileName + ".mp4");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setMetadata(map).setContentType("video/mp4").build();
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials
                .fromStream(serviceAccount)).build().getService();

        try {
            Blob blob = storage.create(blobInfo, file);
            String firebaseUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/"
                    + fileName + ".mp4" + "?alt=media&token=" + blob.getMetadata().get("firebaseStorageDownloadTokens");
            System.out.println("Uploaded file: " + firebaseUrl);
            post.setFirebaseUrl(firebaseUrl);
            postService.save(post);
        } catch (Exception e) {
            System.out.println("File upload failed");
            e.printStackTrace();
        }
    }

    /**
     * Removes old posts from database
     */
    private void removeOldPosts() {
        System.out.println("Program in remove old posts.");

        List<Post> posts = postService.getOldPosts();

        for (Post post : posts) {
            postService.delete(post);
        }
        System.out.println("Deleting old posts done!");
    }

    private void removeOldFirebaseVideos() throws IOException {
        System.out.println("Program in remove old firebase videos.");

        FileInputStream serviceAccount =
                new FileInputStream("{path/to/firebasestorage/adminsdk.json}");
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials
                .fromStream(serviceAccount)).build().getService();
        String bucketName = "Your bucket name including .appspot.com";

        List<Post> posts = postService.getOldFirebaseVideos();
        System.out.println("Post count to be deleted: " + posts.size());

        for (Post post : posts) {
            String blobName = post.getId() + ".mp4";
            BlobId blobId = BlobId.of(bucketName, blobName);
            boolean deleted = storage.delete(blobId);
            if (deleted) {
                postService.delete(post);
            }
        }
        System.out.println("Deleting old firebase videos done!");
    }
}




