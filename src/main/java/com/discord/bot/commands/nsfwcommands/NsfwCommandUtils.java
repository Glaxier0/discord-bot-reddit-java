package com.discord.bot.commands.nsfwcommands;

import com.discord.bot.entity.Post;
import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@AllArgsConstructor
public class NsfwCommandUtils {
    UserService userService;

    public void counter(String userId, String userName, boolean isPorn) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userName);
        }

        if (isPorn) {
            user.setPCount(user.getPCount() + 1);
        } else {
            user.setHCount(user.getHCount() + 1);
        }

        userService.save(user);
    }

    public void checkTypeAndPost(SlashCommandInteractionEvent event, Post post) {
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
