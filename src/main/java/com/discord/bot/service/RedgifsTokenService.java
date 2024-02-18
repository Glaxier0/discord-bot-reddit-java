package com.discord.bot.service;

import com.discord.bot.commands.nsfwcommands.RedgifsCommand;
import com.discord.bot.dto.response.redgifs.RedgifsTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class RedgifsTokenService {
    private static final Logger logger = LoggerFactory.getLogger(RedgifsTokenService.class);
    @Value("${reddit.username}")
    private String REDDIT_USERNAME;
    private final RestTemplate restTemplate;

    public RedgifsTokenService() {
        restTemplate = new RestTemplateBuilder().build();
    }

    public void getAccessToken() {
        URI uri = createUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla:com.glaxier.discordbot:v2 (by /u/" + REDDIT_USERNAME + ")");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RedgifsTokenResponse redgifsTokenResponse = null;
        try {
            redgifsTokenResponse = restTemplate
                    .exchange(uri, HttpMethod.GET, entity, RedgifsTokenResponse.class).getBody();
        } catch (Exception e) {
            logger.error("Error occurred while trying to get access token from redgifs", e);
        }

        RedgifsCommand.TOKEN = redgifsTokenResponse != null ? redgifsTokenResponse.getToken() : null;
        RedgifsCommand.REDDIT_USERNAME = REDDIT_USERNAME;
    }

    private URI createUri() {
        try {
            return new URI("https://api.redgifs.com/v2/auth/temporary");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + "https://api.redgifs.com/v2/auth/temporary", e);
        }
    }
}