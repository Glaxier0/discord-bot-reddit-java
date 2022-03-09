package com.discord.bot.commands.nsfwcommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Post;
import com.discord.bot.entity.User;
import com.discord.bot.service.PostService;
import com.discord.bot.service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class HentaiCommand implements ISlashCommand {
    PostService postService;
    UserService userService;

    Random random = new Random();

    public HentaiCommand(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getTextChannel().isNSFW()) {
            net.dv8tion.jda.api.entities.User user = event.getUser();
            counter(user.getId(), user.getAsTag());
            List<Post> postList = postService.getHentai();
            Post post = postList.get(random.nextInt(postList.size()));
            checkTypeAndPost(event, post);
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Channel is not nsfw.")
                    .setColor(Color.RED).build()).queue();
        }
    }

    private void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag, 0);
        }


        user.setHCount(user.getHCount() + 1);

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
