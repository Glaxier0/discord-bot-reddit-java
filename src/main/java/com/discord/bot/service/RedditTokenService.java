package com.discord.bot.service;

import com.discord.bot.dto.response.reddit.RedditTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class RedditTokenService {
    @Value("${reddit.username}")
    private String REDDIT_USERNAME;
    @Value("${reddit.refresh.token}")
    private String REDDIT_REFRESH_TOKEN;
    @Value("${reddit.client.id}")
    private String REDDIT_CLIENT_ID;
    @Value("${reddit.client.secret}")
    private String REDDIT_CLIENT_SECRET;
    private final RestTemplate restTemplate;

    public RedditTokenService() {
        restTemplate = new RestTemplateBuilder().build();
    }

    public void getAccessToken() {
        URI uri = createUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("User-Agent", "Mozilla:com.glaxier.discordbot:v2 (by /u/" + REDDIT_USERNAME + ")");
        MultiValueMap<String, String> bodyParamMap = new LinkedMultiValueMap<>();
        bodyParamMap.add("grant_type", "refresh_token");
        bodyParamMap.add("refresh_token", REDDIT_REFRESH_TOKEN);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(bodyParamMap, headers);

        var redditTokenResponse = restTemplate.exchange(uri, HttpMethod.POST, entity, RedditTokenResponse.class).getBody();
        assert redditTokenResponse != null;
        SearchReddit.accessToken = redditTokenResponse.getToken();
    }

    private URI createUri() {
        try {
            return new URI("https://www.reddit.com/api/v1/access_token");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + "https://www.reddit.com/api/v1/access_token", e);
        }
    }
}