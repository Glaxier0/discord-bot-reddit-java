package com.discord.bot.service;

import com.discord.bot.commands.nsfwcommands.RedgifsCommand;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Service
public class RedgifsTokenService {
    @Value("${reddit_username}")
    private String REDDIT_USERNAME;
    private final RestTemplate restTemplate;

    public RedgifsTokenService() {
        restTemplate = new RestTemplateBuilder().build();
    }

    public void getAccessToken() {
        URI uri = null;
        try {
            String TOKEN_URL = "https://api.redgifs.com/v2/auth/temporary";
            uri = new URI(TOKEN_URL);
        } catch (URISyntaxException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla:com.glaxier.discordbot:v2 (by /u/" + REDDIT_USERNAME + ")");
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(Objects.requireNonNull(uri),
                HttpMethod.GET, entity, String.class);
        RedgifsCommand.TOKEN = new JsonParser().parse(Objects.requireNonNull(response.getBody())).getAsJsonObject()
                .get("token").getAsString();
        RedgifsCommand.REDDIT_USERNAME = REDDIT_USERNAME;
    }
}