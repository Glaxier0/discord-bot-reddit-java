package com.discord.bot.dto.response.reddit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ChildrenData {
    private String url;
    private String subreddit;
    private String title;
    private String author;
    @JsonProperty("created_utc")
    private long createdUtc;
    @JsonProperty("permalink")
    private String permaUrl;
    @JsonProperty("over_18")
    private boolean isNsfw;
    private RedditMedia media;
}
