package com.discord.bot.commands;

import com.discord.bot.service.PostService;
import com.discord.bot.entity.Post;
import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class RedditCommands extends ListenerAdapter {

    PostService postService;
    UserService userService;
    Random random = new Random();

    public RedditCommands(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "unexpected" -> unexpected(event);
            case "dankmemes" -> dankmemes(event);
            case "memes" -> memes(event);
            case "greentext" -> greentext(event);
            case "blursedimages" -> blursedimages(event);
            case "perfectlycutscreams" -> perfectlycutscreams(event);
            case "interestingasfuck" -> interestingasfuck(event);
            case "facepalm" -> facepalm(event);
        }
    }

    private void unexpected(SlashCommandInteractionEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());
        List<Post> list = postService.getPosts("Unexpected");
        checkTypeAndPost(event, list);
    }

    private void dankmemes(SlashCommandInteractionEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());
        List<Post> list = postService.getPosts("dankmemes");
        checkTypeAndPost(event, list);
    }

    private void memes(SlashCommandInteractionEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());
        List<Post> list = postService.getPosts("memes");
        checkTypeAndPost(event, list);
    }

    private void greentext(SlashCommandInteractionEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());
        List<Post> list = postService.getPosts("greentext");
        checkTypeAndPost(event, list);
    }

    private void blursedimages(SlashCommandInteractionEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());
        List<Post> list = postService.getPosts("blursedimages");
        checkTypeAndPost(event, list);
    }

    private void perfectlycutscreams(SlashCommandInteractionEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());
        List<Post> list = postService.getPosts("perfectlycutscreams");
        checkTypeAndPost(event, list);
    }

    private void interestingasfuck(SlashCommandInteractionEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());
        List<Post> list = postService.getPosts("interestingasfuck");
        checkTypeAndPost(event, list);
    }

    private void facepalm(SlashCommandInteractionEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());
        List<Post> list = postService.getPosts("facepalm");
        checkTypeAndPost(event, list);
    }

    public void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag, 0);
        }
        user.setRedditCount(user.getRedditCount() + 1);
        userService.save(user);
    }

    private void checkTypeAndPost(SlashCommandInteractionEvent event, List<Post> list) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Post post = list.get(random.nextInt(list.size()));

        switch (post.getContentType()) {
            case "video" -> {
                while (post.getFirebaseUrl() == null && (post.getContentType().equals("video"))) {
                    post = list.get(random.nextInt(list.size()));
                }
                embedBuilder.setTitle(post.getTitle(), post.getPermaUrl())
                        .setFooter("Posted in r/" + post.getSubreddit() + " by u/" + post.getAuthor());
                event.reply(post.getFirebaseUrl()).queue();
                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
            }
            case "gif", "image" -> {
                embedBuilder.setTitle(post.getTitle(), post.getPermaUrl())
                        .setImage(post.getUrl())
                        .setFooter("Posted in r/" + post.getSubreddit() + " by u/" + post.getAuthor());
                event.replyEmbeds(embedBuilder.build()).queue();
            }
        }

    }
}

