package com.discord.bot.commands;

import com.discord.bot.service.PostService;
import com.discord.bot.entity.Post;
import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class NsfwCommands extends ListenerAdapter {

    PostService postService;
    UserService userService;
    Random random = new Random();

    public NsfwCommands(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "hentai" -> hentai(event);
            case "porn" -> porn(event);
        }
    }

    private void hentai(SlashCommandInteractionEvent event) {
        if (event.getTextChannel().isNSFW()) {
            net.dv8tion.jda.api.entities.User user = event.getUser();
            counter(user.getId(), user.getAsTag(), true);
            List<Post> postList = postService.getHentai();
            Post post = postList.get(random.nextInt(postList.size()));
            checkTypeAndPost(event, post);
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Channel is not nsfw.")
                    .setColor(Color.RED).build()).queue();
        }

    }

    private void porn(SlashCommandInteractionEvent event) {
        if (event.getTextChannel().isNSFW()) {
            net.dv8tion.jda.api.entities.User user = event.getUser();
            counter(user.getId(), user.getAsTag(), false);
            List<Post> postList = postService.getPorn();
            Post post = postList.get(random.nextInt(postList.size()));
            checkTypeAndPost(event, post);
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Channel is not nsfw. Please write command in nsfw channel.")
                    .setColor(Color.RED).build()).queue();
        }
    }

    private void counter(String userId, String userWithTag, boolean isHentai) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag, 0);
        }

        if (isHentai) {
            user.setHCount(user.getHCount() + 1);
        } else {
            user.setPCount(user.getPCount() + 1);
        }
        userService.save(user);
    }

    private void checkTypeAndPost(SlashCommandInteractionEvent event, Post post) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (post.getUrl().contains("redgifs.com")
                || (post.getUrl().contains("imgur.com") && post.getUrl().contains(".gifv"))
                || post.getUrl().contains("gfycat.com")) {
            post.setContentType("video");
        }

        switch (post.getContentType()) {
            case "video" -> event.reply(post.getUrl()).queue();
            case "image" -> {
                embedBuilder.setTitle(post.getTitle(), post.getPermaUrl())
                        .setImage(post.getUrl())
                        .setFooter("Posted in r/" + post.getSubreddit() + " by u/" + post.getAuthor());
                event.replyEmbeds(embedBuilder.build()).queue();
            }
        }
    }
}
