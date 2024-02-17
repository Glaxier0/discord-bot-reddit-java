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
public class RedditVideo {
    private int duration;
    @JsonProperty("fallback_url")
    private String fallbackUrl;
}
