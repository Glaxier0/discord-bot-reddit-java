package com.example.DiscordBot;

import com.clickntap.vimeo.Vimeo;
import com.clickntap.vimeo.VimeoException;
import com.example.DiscordBot.Entity.Post;
import com.example.DiscordBot.Listener.EventListener;
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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
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

/**
 * Main class that have methods for bot.
 */
public class Bot {
    public static void main(String[] args) {

        Timer hours = new Timer();
        Timer days = new Timer();

        //Runs every hours
        hours.schedule(new TimerTask() {
            @Override
            public void run() {
                SessionFactory factory = new Configuration()
                        .configure("hibernate.cfg.xml")
                        .addAnnotatedClass(Post.class)
                        .buildSessionFactory();

                searchReddit(factory);
                downloadAndUploadToVimeo(factory);

                factory.close();
            }
        }, 0, 3600000);

        //Runs every day
        days.schedule(new TimerTask() {
            @Override
            public void run() {
                SessionFactory factory = new Configuration()
                        .configure("hibernate.cfg.xml")
                        .addAnnotatedClass(Post.class)
                        .buildSessionFactory();

                removeOldPosts(factory);
                factory.close();
            }
        }, 0, 86400000);

        startDiscordBot();
    }

    /**
     * Search specialized subreddits and save them to database
     */
    private static void searchReddit(SessionFactory factory) {

        final String REDDIT_USERNAME = "your reddit username";
        final String REDDIT_PASSWORD = "your reddit password";
        final String CLIENT_ID = "your reddit client id";
        final String CLIENT_SECRET = "your reddit client secret";

        UserAgent userAgent = new UserAgent("Chrome", "com.example.DiscordBot.bot",
                "v0.1", REDDIT_USERNAME);

        NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
        RedditClient redditClient = OAuthHelper.automatic(networkAdapter,
                Credentials.script(REDDIT_USERNAME, REDDIT_PASSWORD,
                        CLIENT_ID, CLIENT_SECRET));

        //Edit memes to desired subreddit.
        DefaultPaginator<Submission> memes =
                redditClient.subreddit("memes")
                        .posts()
                        .sorting(SubredditSort.TOP)
                        .timePeriod(TimePeriod.DAY)
                        .build();

        Listing<Submission> submissions = memes.next();

        //List subreddit posts and save them to database
        for (Submission s : submissions) {
            if (!s.isNsfw()) {

                //Char limit for uploading vimeo title and description
                int charLimit = s.getTitle().length() + s.getAuthor().length() + s.getSubreddit().length();

                Post post = new Post(s.getUrl(), s.getSubreddit(), s.getTitle(), s.getAuthor(), s.getCreated());
                post.setPermaUrl("https://reddit.com" + s.getPermalink());

                //Determines if reddit post is video
                if (s.getUrl().length() == 31 && (s.getUrl().contains("https://v.redd.it")) && charLimit <= 101) {

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

                //Checks if post exists in database
                Session session = factory.getCurrentSession();
                session.beginTransaction();

                Query query = session.createQuery("SELECT e.url from Post e where e.url=:url");
                query.setParameter("url", post.getUrl());
                String queryPost = (String) query.uniqueResult();

                session.getTransaction().commit();
                session.close();

                if (post.getUrl().equals(queryPost)) {
                    System.out.println("URL exist in database: " + queryPost);
                } else {
                    session = factory.getCurrentSession();
                    session.beginTransaction();
                    System.out.println("Saved url to database: " + post.getUrl());
                    session.save(post);
                    session.getTransaction().commit();
                    session.close();
                }
            }
        }
        System.out.println("Reddit search done!");
    }

    /**
     * Removes old posts from database and vimeo
     */
    private static void removeOldPosts(SessionFactory factory) {

        final String VIMEO_TOKEN = "your vimeo token";
        Date date = new Date();
        int dayDiff = 3;

        Session session = factory.getCurrentSession();
        session.beginTransaction();

        Query query = session.createQuery("SELECT e FROM Post e");
        List<Post> list = query.getResultList();

        session.getTransaction().commit();
        session.close();

        for (Post post : list) {
            //Get posts older or equals to 3 day and deletes them.
            if (Math.abs(date.getDate() - post.getCreated().getDate()) >= dayDiff) {

                //if post is video then its uploaded video in vimeo needs to be deleted too.
                if (post.getContentType() != null && post.getContentType().equals("video") && post.getVimeoUrl() != null) {

                    Vimeo vimeo = new Vimeo(VIMEO_TOKEN);

                    String videoEndPoint = "/videos" + post.getVimeoUrl().substring(17);

                    try {
                        vimeo.removeVideo(videoEndPoint);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                session = factory.getCurrentSession();
                session.beginTransaction();
                session.delete(post);
                session.getTransaction().commit();
                session.close();
            }
        }

        System.out.println("Deleting old posts done!");
    }

    /**
     * Starts the discord bot
     */
    private static void startDiscordBot() {
        final String DISCORD_TOKEN = "your discord bot token";
        try {
            JDA jda = JDABuilder.createDefault(DISCORD_TOKEN).build();
            jda.addEventListener(new EventListener());
        } catch (LoginException e) {
            e.printStackTrace();
        }
        System.out.println("Starting bot is done!");
    }

    /**
     * Checks database if vimeo url exists
     * if not download video from reddit url and
     * upload it to imgur and set vimeo_url to database
     */
    private static void downloadAndUploadToVimeo(SessionFactory factory) {

        final String VIMEO_TOKEN = "your vimeo token";

        Vimeo vimeo = new Vimeo(VIMEO_TOKEN);

        Session session = factory.getCurrentSession();
        session.beginTransaction();

        Query query = session.createQuery("SELECT e FROM Post e WHERE e.contentType = 'video' AND e.vimeoUrl IS NULL");
        List<Post> list = query.getResultList();

        session.getTransaction().commit();
        session.close();

        for (Post post : list) {

            //Gets download url from database
            URL url = null;
            try {
                url = new URL(post.getDownloadUrl());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //Download file and save it as temp.mp4
            try (InputStream in = url.openStream();
                 ReadableByteChannel rbc = Channels.newChannel(in);
                 FileOutputStream fos = new FileOutputStream("temp.mp4")) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String videoEndPoint = null;

            //Upload video to vimeo
            try {
                videoEndPoint = vimeo.addVideo(new File("temp.mp4"), false);
                vimeo.updateVideoMetadata(videoEndPoint, "Posted on: r/" + post.getSubreddit() +
                                " by: u/" + post.getAuthor() + "\nTitle: " + post.getTitle(),
                        "This video taken from reddit. Link to the video: " + post.getPermaUrl(),
                        "", "anybody", "public", false);

            } catch (VimeoException | IOException e) {
                e.printStackTrace();
            }

            String vimeoUrl = "https://vimeo.com" + videoEndPoint.substring(7);

            post.setVimeoUrl(vimeoUrl);

            session = factory.getCurrentSession();
            session.beginTransaction();
            session.saveOrUpdate(post);
            session.getTransaction().commit();
            session.close();
        }

        //Deletes excess file
        File file = new File("temp.mp4");
        if (file.delete()) {
            System.out.println("File deleted successfully");
        }

        System.out.println("File uploading to vimeo is done!");
    }
}




