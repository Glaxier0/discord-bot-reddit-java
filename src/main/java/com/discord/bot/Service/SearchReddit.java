package com.discord.bot.service;

import com.discord.bot.entity.Post;
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
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Service
public class SearchReddit {
    public static String ACCESS_TOKEN;
    @Value("${reddit_username}")
    private String REDDIT_USERNAME;

    List<String> subreddits = Arrays.asList("Unexpected", "memes", "dankmemes", "greentext",
            "blursedimages", "perfectlycutscreams", "interestingasfuck", "facepalm",
            //Hentai Subreddits
            "hentai", "HENTAI_GIF", "rule34", "Tentai", "hentaibondage",
            //Porn Subreddits
            "porninaminute", "porninfifteenseconds", "porn", "NSFW_GIF",
            "nsfw_gifs", "porn_gifs", "anal_gifs", "Doggystyle_NSFW",
            //Tits Subreddits
            "Boobies", "TittyDrop", "boobs");

    private RestTemplate restTemplate;
    PostService postService;

    public SearchReddit(PostService postService) {
        restTemplate = new RestTemplateBuilder().build();
        this.postService = postService;
    }

    public void searchReddit() {
        System.out.println("Program in search reddit.");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(ACCESS_TOKEN);
        httpHeaders.set("User-Agent", "Mozilla:com.glaxier.discordbot:v2 (by /u/" + REDDIT_USERNAME + ")");
        HttpEntity<String> bearerHeader = new HttpEntity<>(null, httpHeaders);
        String REDDIT_URL;
        String baseDownloadUrl = "https://sd.redditsave.com/download.php?permalink=https://reddit.com";
        for (String subredditName : subreddits) {
            REDDIT_URL = "https://oauth.reddit.com/r/" + subredditName + "/top.json?limit=25&t=day&raw_json=1";
            URI reddit_uri = null;
            try {
                reddit_uri = new URI(REDDIT_URL);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            ResponseEntity<String> responseEntity = restTemplate.exchange(reddit_uri, HttpMethod.GET, bearerHeader, String.class);
            JsonArray redditPosts = new JsonParser().parse(responseEntity.getBody()).getAsJsonObject()
                    .get("data").getAsJsonObject().getAsJsonArray("children");

            for (JsonElement jsonElement : redditPosts) {
                JsonElement redditPost = jsonElement.getAsJsonObject().get("data");
                String url = redditPost.getAsJsonObject().get("url").getAsString();
                String subreddit = redditPost.getAsJsonObject().get("subreddit").getAsString();
                String title = redditPost.getAsJsonObject().get("title").getAsString();
                String author = redditPost.getAsJsonObject().get("author").getAsString();
                long timestamp = redditPost.getAsJsonObject().get("created_utc").getAsLong() * 1000L;
                Date created = new Date(new Timestamp(timestamp).getTime());
                String permalink = redditPost.getAsJsonObject().get("permalink").getAsString();
                boolean isNSFW = redditPost.getAsJsonObject().get("over_18").getAsBoolean();
                Post post = new Post(url, subreddit, title, author, created, "https://reddit.com" + permalink);
                if (!isNSFW) {
                    JsonElement media = redditPost.getAsJsonObject().get("media");
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
                            }

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
                String postServiceByPermalink = postService.getByPermaUrl(post.getPermalink());

                if (postServiceByUrl == null && postServiceByPermalink == null) {
                    postService.save(post);
                }
            }
        }
        System.out.println("Reddit search done!");
    }
}
