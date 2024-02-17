package com.discord.bot.service;

import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Service
public class RedditTokenService {
    @Value("${reddit_username}")
    private String REDDIT_USERNAME;
    @Value("${reddit_refresh_token}")
    private String REDDIT_REFRESH_TOKEN;
    @Value("${reddit_client_id}")
    private String REDDIT_CLIENT_ID;
    @Value("${reddit_client_secret}")
    private String REDDIT_CLIENT_SECRET;
    private final RestTemplate restTemplate;

    public RedditTokenService() {
        restTemplate = new RestTemplateBuilder().build();
    }

    public void getAccessToken() {
        URI uri = null;
        try {
            String TOKEN_URL = "https://www.reddit.com/api/v1/access_token";
            uri = new URI(TOKEN_URL);
        } catch (URISyntaxException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("User-Agent", "Mozilla:com.glaxier.discordbot:v2 (by /u/" + REDDIT_USERNAME + ")");
        MultiValueMap<String, String> bodyParamMap = new LinkedMultiValueMap<>();
        bodyParamMap.add("grant_type", "refresh_token");
        bodyParamMap.add("refresh_token", REDDIT_REFRESH_TOKEN);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(bodyParamMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(Objects.requireNonNull(uri),
                HttpMethod.POST, entity, String.class);
        SearchReddit.ACCESS_TOKEN = new JsonParser().parse(Objects.requireNonNull(response.getBody())).getAsJsonObject()
                .get("access_token").getAsString();
    }
}