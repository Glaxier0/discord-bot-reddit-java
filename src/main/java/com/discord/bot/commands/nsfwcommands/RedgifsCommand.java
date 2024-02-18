package com.discord.bot.commands.nsfwcommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.dto.response.redgifs.RedgifsResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RedgifsCommand implements ISlashCommand {
    private static final Logger logger = LoggerFactory.getLogger(RedgifsCommand.class);
    public static String TOKEN;
    public static String REDDIT_USERNAME;
    final NsfwCommandUtils utils;

    public RedgifsCommand(NsfwCommandUtils utils) {
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sort = Objects.requireNonNull(event.getOption("sort")).getAsString();
        String tag = Objects.requireNonNull(event.getOption("tag")).getAsString().toLowerCase(Locale.ROOT);
        String encodedTag = URLEncoder.encode(tag, StandardCharsets.UTF_8);
        String redgifsUrl;

        if (sort.equals("1")) {
            redgifsUrl = "https://api.redgifs.com/v2/gifs/search?order=trending&type=g&count=80&search_text=" + encodedTag;
        } else {
            redgifsUrl = "https://api.redgifs.com/v2/gifs/search?order=best&type=g&count=80&search_text=" + encodedTag;
        }

        URI redgifsUri = createUri(redgifsUrl);

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(TOKEN);
        httpHeaders.set("User-Agent", "Mozilla:com.glaxier.discordbot:v2 (by /u/" + REDDIT_USERNAME + ")");
        HttpEntity<String> bearerHeader = new HttpEntity<>(httpHeaders);

        var response = restTemplate.exchange(redgifsUri, HttpMethod.GET, bearerHeader, RedgifsResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            var redgifsResponse = response.getBody();
            assert redgifsResponse != null;
            event.reply("https://www.redgifs.com/watch/" +
                    redgifsResponse.getRedgifs()
                            .get(new Random().nextInt(redgifsResponse.getRedgifs().size() - 1))
                            .getId()).queue();
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Didn't find any post with " + tag + " tag.")
                    .setColor(Color.RED).build()).queue();
            logger.warn("Could not find any gifs with tag: " + tag + "encoded tag: " + encodedTag);
        }
        User user = event.getUser();
        utils.counter(user.getId(), user.getAsTag(), true);
    }

    private URI createUri(String redgifsUrl) {
        try {
            return new URI(redgifsUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + redgifsUrl, e);
        }
    }
}
