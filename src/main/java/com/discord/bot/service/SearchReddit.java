package com.discord.bot.service;

import com.discord.bot.entity.Post;
import com.discord.bot.entity.Subreddit;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.Objects;

@Service
public class SearchReddit {
    public static String accessToken;
    @Value("${reddit.username}")
    private String redditUsername;

    private final RestTemplate restTemplate;
    PostService postService;
    SubredditService subredditService;

    public SearchReddit(PostService postService, SubredditService subredditService) {
        restTemplate = new RestTemplateBuilder().build();
        this.postService = postService;
        this.subredditService = subredditService;
    }

    public void searchReddit() {
        List<Subreddit> subreddits = subredditService.getSubreddits();

        System.out.println("Program in search reddit.");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        httpHeaders.set("User-Agent", "Mozilla:com.glaxier.discordbot:v2 (by /u/" + redditUsername + ")");
        HttpEntity<String> bearerHeader = new HttpEntity<>(null, httpHeaders);

        String REDDIT_URL;
        String baseDownloadUrl = "https://sd.redditsave.com/download.php?permalink=https://reddit.com";

        for (Subreddit subreddit : subreddits) {
            REDDIT_URL = "https://oauth.reddit.com/r/" + subreddit.getName() + "/top.json?limit=25&t=day&raw_json=1";
            URI reddit_uri;
            try {
                reddit_uri = new URI(REDDIT_URL);
            } catch (URISyntaxException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
                continue;
            }

            ResponseEntity<String> responseEntity;
            try {
                responseEntity = restTemplate.exchange(Objects.requireNonNull(reddit_uri),
                        HttpMethod.GET, bearerHeader, String.class);
            } catch (HttpClientErrorException.NotFound e) {
                System.out.println("Subreddit not found: " + subreddit);
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
                continue;
            }

            JsonArray redditPosts = new JsonParser().parse(Objects.requireNonNull(responseEntity.getBody()))
                    .getAsJsonObject().get("data").getAsJsonObject().getAsJsonArray("children");

            for (JsonElement jsonElement : redditPosts) {
                JsonObject redditPost = jsonElement.getAsJsonObject().get("data").getAsJsonObject();
                String url = redditPost.get("url").getAsString();
                String subredditName = redditPost.get("subreddit").getAsString();
                String title = redditPost.get("title").getAsString();
                String author = redditPost.get("author").getAsString();
                long timestamp = redditPost.get("created_utc").getAsLong() * 1000L;
                Date created = new Date(new Timestamp(timestamp).getTime());
                String permalink = redditPost.get("permalink").getAsString();
                boolean isNSFW = redditPost.get("over_18").getAsBoolean();
                Post post = new Post(url, subredditName, title, author, created, "https://reddit.com" + permalink);
                if (!isNSFW) {
                    JsonElement media = redditPost.get("media");
                    if (url.contains("https://v.redd.it") && !media.isJsonNull()) {
                        if (!(media.getAsJsonObject().get("reddit_video") == null)) {
                            JsonObject redditVideo = media.getAsJsonObject()
                                    .get("reddit_video").getAsJsonObject();
                            int duration = redditVideo.get("duration").getAsInt();
                            if (duration <= 60) {
                                post.setContentType("video");
                                String fallbackVideoUrl = redditVideo.get("fallback_url").getAsString();
                                String fallbackAudioUrl = fallbackVideoUrl.substring(0, fallbackVideoUrl.indexOf("DASH_"))
                                        + "DASH_audio.mp4?source=fallback";
                                String downloadURL = baseDownloadUrl + permalink + "&video_url=" + fallbackVideoUrl + "&audio_url="
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
                String postServiceByUrl = postService.getByUrl(post.getUrl());
                String postServiceByPermalink = postService.getByPermaUrl(post.getPermaUrl());

                if (postServiceByUrl == null && postServiceByPermalink == null) {
                    postService.save(post);
                }
            }
        }
        System.out.println("Reddit search done!");
    }
}
