package com.discord.bot;

import com.clickntap.vimeo.Vimeo;
import com.clickntap.vimeo.VimeoException;
import com.discord.bot.Entity.Post;
import com.discord.bot.Event.*;
import com.discord.bot.Service.PostService;
import com.discord.bot.Service.TodoService;
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
import java.io.File;
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

    PostService service;
    TodoService todoService;
    //Edit this fields in application.properties under main > resources
    @Value("${reddit_username}")
    private String REDDIT_USERNAME;
    @Value("${reddit_password}")
    private String REDDIT_PASSWORD;
    @Value("${reddit_client_id}")
    private String REDDIT_CLIENT_ID;
    @Value("${reddit_client_secret}")
    private String REDDIT_CLIENT_SECRET;
    @Value("${vimeo_token}")
    private String VIMEO_TOKEN;
    @Value("${discord_bot_token}")
    private String DISCORD_TOKEN;

    public Bot(PostService service, TodoService todoService) {
        this.service = service;
        this.todoService = todoService;
    }

    @Bean
    public void startDiscordBot() {
        try {
            JDA jda = JDABuilder.createDefault(DISCORD_TOKEN).build();
            jda.getPresence().setActivity(Activity.playing("Type !help"));
            jda.addEventListener(new RedditCommands(service), new TextCommands(), new AdminCommands(service),
                    new NsfwCommands(service), new ToDoCommands(todoService));

            System.out.println("Starting bot is done!");
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 3600000)
    public void hourDelay() {
        searchReddit();
        downloadAndUploadToVimeo();
    }

    /**
     * Searchs specialized subreddits and save them to database
     */
    private void searchReddit() {
        System.out.println("Program in search reddit");

        UserAgent userAgent = new UserAgent("Chrome", "com.example.demo.bot",
                "v0.1", REDDIT_USERNAME);
        NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
        RedditClient redditClient = OAuthHelper.automatic(networkAdapter,
                Credentials.script(REDDIT_USERNAME, REDDIT_PASSWORD,
                        REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET));

        //Embedded link need to be embedable in discord to nsfw videos work. Like redgifs.com or gyfcat.
        //For legal reasons you cant upload vimeo nsfw stuff
        //Keep in mind this things adding more nsfw subreddits to list.
        List<String> subreddits = Arrays.asList("Unexpected", "memes", "dankmemes", "greentext",
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

                int charLimit = s.getTitle().length() + s.getAuthor().length() + s.getSubreddit().length();
                Post post = new Post(s.getUrl(), s.getSubreddit(), s.getTitle(), s.getAuthor(), s.getCreated());
                post.setPermaUrl("https://reddit.com" + s.getPermalink());

                if (!s.isNsfw()) {
                    if (s.getUrl().contains("https://v.redd.it") && charLimit <= 101) {

                        String fallbackUrl = Objects.requireNonNull(Objects.requireNonNull(s.getEmbeddedMedia()).getRedditVideo()).getFallbackUrl();
                        String fallbackVideo = fallbackUrl.substring(0, fallbackUrl.indexOf("?"));
                        String fallbackAudio = fallbackVideo.substring(0, fallbackVideo.indexOf("_") + 1) + "audio.mp4";
                        String baseDownloadUrl = "https://ds.redditsave.com/download.php?permalink=https://reddit.com";
                        String videoDownloadUrl = baseDownloadUrl + s.getPermalink() + "&video_url=";
                        String fallbackVideoDownloadUrl = videoDownloadUrl + fallbackUrl + "&audio_url=";
                        String fallbackVideoWithAudioDownloadUrl = fallbackVideoDownloadUrl + fallbackAudio + "?source=fallback";

                        post.setContentType("video");
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

                String queryPost = service.getByUrl(post.getUrl());

                if (post.getUrl().equals(queryPost)) {
                    System.out.println("URL exist in database: " + queryPost);
                } else {
                    service.save(post);
                    System.out.println("Url saved to database: " + post.getUrl());
                }
            }

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Reddit search done!");
    }

    /**
     * Checks database if vimeo url exists
     * if not download video from reddit url and
     * upload it to imgur and set vimeo_url to database
     */
    private void downloadAndUploadToVimeo() {
        System.out.println("Program in download and upload to vimeo");
        Vimeo vimeo = new Vimeo(VIMEO_TOKEN);

        List<Post> list = service.getVideoNullVimeo();
        System.out.println("File count: " + list.size());

        for (Post post : list) {

            //Gets download url from database
            URL url = null;
            try {
                url = new URL(post.getDownloadUrl());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //Download file and save it as temp.mp4
            try (InputStream in = Objects.requireNonNull(url).openStream();
                 ReadableByteChannel rbc = Channels.newChannel(in);
                 FileOutputStream fos = new FileOutputStream("temp.mp4")) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Upload video to vimeo
            String videoEndPoint, vimeoUrl = null;

            try {
                videoEndPoint = vimeo.addVideo(new File("temp.mp4"), false);
                vimeo.updateVideoMetadata(videoEndPoint, "Title: " + post.getTitle() + " by: u/" + post.getAuthor() +
                                "\nPosted on: r/" + post.getSubreddit(),
                        "This video taken from reddit. Link to the video: " + post.getPermaUrl(),
                        "", "anybody", "public", false);
                System.out.println("Id of uploaded video to vimeo: " + post.getId() + " url: " + post.getUrl());
                vimeoUrl = "https://vimeo.com" + Objects.requireNonNull(videoEndPoint).substring(7);
            } catch (VimeoException | IOException e) {
                e.printStackTrace();
                System.out.println("Video upload failed: " + post.getId() + " url: " + post.getUrl());
            }

            post.setVimeoUrl(vimeoUrl);

            service.save(post);
        }

        //Deletes excess file
        File file = new File("temp.mp4");
        if (file.delete()) {
            System.out.println("File deleted successfully");
        }
        System.out.println("File uploading to vimeo is done!");
    }

    /**
     * Removes old posts from database
     */
    @Scheduled(fixedDelay = 86400000)
    private void removeOldPosts() {
        System.out.println("Program in remove old posts");
        Date date = new Date();
        final int dayDiff = 4;

        List<Post> list = service.findAll();

        for (Post post : list) {

            if (Math.abs(date.getDate() - post.getCreated().getDate()) >= dayDiff) {

                if (post.getContentType() != null && post.getContentType().equals("video") && post.getVimeoUrl() != null) {

                    Vimeo vimeo = new Vimeo(VIMEO_TOKEN);

                    String videoEndPoint = "/videos" + post.getVimeoUrl().substring(17);

                    try {
                        vimeo.removeVideo(videoEndPoint);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                service.delete(post);
            }
        }
        System.out.println("Deleting old posts done!");
    }
}




