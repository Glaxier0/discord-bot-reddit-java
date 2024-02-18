package com.discord.bot.service;

import com.discord.bot.dto.response.reddit.RedditResponse;
import com.discord.bot.entity.Post;
import com.discord.bot.entity.Subreddit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Service
public class SearchReddit {
    public static String accessToken;
    @Value("${reddit.username}")
    private String redditUsername;

    private final RestTemplate restTemplate;
    final PostService postService;
    final SubredditService subredditService;

    public SearchReddit(PostService postService, SubredditService subredditService) {
        restTemplate = new RestTemplateBuilder().build();
        this.postService = postService;
        this.subredditService = subredditService;
    }

    public void searchReddit() {
        System.out.println("Program in search reddit.");
        List<Subreddit> subreddits = subredditService.getSubreddits();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        httpHeaders.set("User-Agent", "Mozilla:com.glaxier.discordbot:v2 (by /u/" + redditUsername + ")");
        HttpEntity<String> bearerHeader = new HttpEntity<>(null, httpHeaders);

        String baseDownloadUrl = "https://sd.redditsave.com/download.php?permalink=https://reddit.com";

        for (Subreddit subreddit : subreddits) {
            var redditUrl = "https://oauth.reddit.com/r/" + subreddit.getName() + "/top.json?limit=25&t=day&raw_json=1";
            URI reddit_uri = createUri(redditUrl);

            ResponseEntity<RedditResponse> response;
            try {
                response = restTemplate.exchange(reddit_uri, HttpMethod.GET, bearerHeader, RedditResponse.class);
            } catch (HttpClientErrorException e) {
                System.out.println("Subreddit not found: " + subreddit);
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
                continue;
            }

            var redditResponse = response.getBody();
            assert redditResponse != null;
            var redditPosts = redditResponse.getData().getChildren();

            for (var children : redditPosts) {
                var redditPost = children.getData();

                String url = redditPost.getUrl();
                String subredditName = redditPost.getSubreddit();
                String title = redditPost.getTitle();
                String author = redditPost.getAuthor();
                String permaUrl = redditPost.getPermaUrl();
                boolean isNSFW = redditPost.isNsfw();

                long timestamp = redditPost.getCreatedUtc() * 1000L;
                Date created = new Date(new Timestamp(timestamp).getTime());

                Post post = new Post(url, subredditName, title, author, created, "https://reddit.com" + permaUrl);

                if (!isNSFW) {
                    var media = redditPost.getMedia();
                    if (url.contains("https://v.redd.it") && media != null) {
                        var redditVideo = media.getRedditVideo();
                        if (redditVideo != null) {
                            int duration = redditVideo.getDuration();
                            if (duration <= 60) {
                                post.setContentType("video");
                                String fallbackVideoUrl = redditVideo.getFallbackUrl();
                                String fallbackAudioUrl = fallbackVideoUrl.substring(0, fallbackVideoUrl.indexOf("DASH_"))
                                        + "DASH_audio.mp4?source=fallback";
                                String downloadURL = baseDownloadUrl + permaUrl + "&video_url=" + fallbackVideoUrl + "&audio_url="
                                        + fallbackAudioUrl;
                                post.setDownloadUrl(downloadURL);
                            } else
                                continue;
                        }
                    }
                    if (url.contains(".gif") || url.contains("gfycat.com")) {
                        post.setContentType("gif");
                    } else if (url.contains(".jpg") || url.contains(".png")) {
                        post.setContentType("image");
                    } else if (url.contains("https://www.reddit.com/")) {
                        continue;
                    }
                } else {
                    if (url.contains("https://www.reddit.com/")) {
                        continue;
                    } else {
                        post.setContentType("image");
                    }
                }

                if (!postService.existsByUrlAndPermaUrl(url, permaUrl)) {
                    postService.save(post);
                }
            }
        }
        System.out.println("Reddit search done!");
    }

    private URI createUri(String redditUrl) {
        try {
            return new URI(redditUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + redditUrl, e);
        }
    }
}
