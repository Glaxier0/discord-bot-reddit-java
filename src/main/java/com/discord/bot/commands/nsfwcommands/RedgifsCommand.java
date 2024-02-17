package com.discord.bot.commands.nsfwcommands;

import com.discord.bot.commands.ISlashCommand;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    public static String TOKEN;
    public static String REDDIT_USERNAME;
    NsfwCommandUtils utils;

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

        URI redgifsUri = null;
        try {
            redgifsUri = new URI(redgifsUrl);
        } catch (URISyntaxException e) {
            System.out.println("Error: " + redgifsUrl + " " + tag + " " + encodedTag);
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(TOKEN);
        httpHeaders.set("User-Agent", "Mozilla:com.glaxier.discordbot:v2 (by /u/" + REDDIT_USERNAME + ")");
        HttpEntity<String> bearerHeader = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> redgifsEntity = restTemplate.exchange(Objects.requireNonNull(redgifsUri), HttpMethod.GET, bearerHeader, String.class);

        if (redgifsEntity.getStatusCodeValue() == 200) {
            JsonArray jsonArray = new JsonParser().parse(Objects.requireNonNull(redgifsEntity.getBody())).getAsJsonObject().getAsJsonArray("gifs");
            event.reply("https://www.redgifs.com/watch/" + jsonArray.get(new Random().nextInt(jsonArray.size() - 1))
                    .getAsJsonObject().get("id").getAsString()).queue();
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Didn't find any post with " + tag + " tag.")
                    .setColor(Color.RED).build()).queue();
            System.out.println("tag: " + tag + "encoded tag: " + encodedTag);
        }
        User user = event.getUser();
        utils.counter(user.getId(), user.getAsTag(), true);
    }
}
