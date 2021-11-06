package com.discord.bot.Service;

import com.discord.bot.Entity.Post;
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
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class SearchReddit {
    /**
     * Searchs specialized subreddits and save them to database
     */
    public void searchReddit(PostService postService, String REDDIT_USERNAME, String REDDIT_PASSWORD,
                             String REDDIT_CLIENT_ID, String REDDIT_CLIENT_SECRET) {

        System.out.println("Program in search reddit.");

        UserAgent userAgent = new UserAgent("Chrome", "com.discord.bot",
                "v1.0", REDDIT_USERNAME);
        NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
        RedditClient redditClient = OAuthHelper.automatic(networkAdapter,
                Credentials.script(REDDIT_USERNAME, REDDIT_PASSWORD,
                        REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET));

        //Embedded link need to be embedable in discord to nsfw videos work. Like redgifs.com or gyfcat.
        //Keep in mind these things adding more nsfw subreddits to list.
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
}
