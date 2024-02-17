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
public class RedditTokenResponse {
    @JsonProperty("access_token")
    private String token;
    @JsonProperty("token_type")
    private String type;
    @JsonProperty("expires_in")
    private String expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String scope;
}
