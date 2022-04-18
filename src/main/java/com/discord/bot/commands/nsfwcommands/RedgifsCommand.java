package com.discord.bot.commands.nsfwcommands;

import com.discord.bot.commands.ISlashCommand;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Random;

public class RedgifsCommand implements ISlashCommand {
    NsfwCommandUtils utils;

    Random random = new Random();

    public RedgifsCommand(NsfwCommandUtils utils) {
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getTextChannel().isNSFW()) {
            String sort = event.getOption("sort").getAsString();
            String tag = event.getOption("tag").getAsString().toLowerCase(Locale.ROOT);
            String encodedTag = URLEncoder.encode(tag, StandardCharsets.UTF_8);
            String redgifsUrl;
            if (sort.equals("Trending")) {
                redgifsUrl = "https://api.redgifs.com/v2/gifs/search?search_text=" + encodedTag +
                        "&order=trending&type=g&count=80";
            } else {
                redgifsUrl = "https://api.redgifs.com/v2/gifs/search?search_text=" + encodedTag +
                        "&order=top28&type=g&count=80";
            }

            URI redgifsUri = null;
            try {
                redgifsUri = new URI(redgifsUrl);
            } catch (URISyntaxException e) {
                System.out.println("Error: " + redgifsUrl + " " + tag + " " + encodedTag);
                e.printStackTrace();
            }

            RestTemplate restTemplate = new RestTemplateBuilder().build();
            ResponseEntity<String> redgifsEntity = restTemplate.getForEntity(redgifsUri, String.class);
            if (redgifsEntity.getStatusCodeValue() == 200) {
                JsonArray jsonArray = new JsonParser().parse(redgifsEntity.getBody()).getAsJsonObject().getAsJsonArray("gifs");
                event.reply("https://www.redgifs.com/watch/" + jsonArray.get(random.nextInt(jsonArray.size() -1))
                        .getAsJsonObject().get("id").getAsString()).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder().setDescription("Didn't find any post with " + tag + " tag.")
                        .setColor(Color.RED).build()).queue();
                System.out.println("tag: " + tag + "encoded tag: " + encodedTag);
            }
            net.dv8tion.jda.api.entities.User user = event.getUser();
            utils.counter(user.getId(), user.getAsTag(), true);
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Channel is not nsfw.")
                    .setColor(Color.RED).build()).queue();
        }
    }
}
