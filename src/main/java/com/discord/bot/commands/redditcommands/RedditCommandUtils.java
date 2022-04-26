package com.discord.bot.commands.redditcommands;

import com.discord.bot.entity.Post;
import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;
import java.util.Random;

public class RedditCommandUtils {
    UserService userService;

    Random random = new Random();

    public RedditCommandUtils(UserService userService) {
        this.userService = userService;
    }

    public void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag);
        }
        user.setRedditCount(user.getRedditCount() + 1);
        userService.save(user);
    }

    public void checkTypeAndPost(SlashCommandInteractionEvent event, List<Post> list) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Post post = list.get(random.nextInt(list.size()));

        switch (post.getContentType()) {
            case "video" -> {
                while (post.getFirebaseUrl() == null && (post.getContentType().equals("video"))) {
                    post = list.get(random.nextInt(list.size()));
                }
                embedBuilder.setTitle(post.getTitle(), post.getPermalink())
                        .setFooter("Posted in r/" + post.getSubreddit() + " by u/" + post.getAuthor());
                event.reply(post.getFirebaseUrl()).queue();
                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
            }
            case "gif", "image" -> {
                if (post.getUrl().contains(".gifv")) {
                    embedBuilder.setTitle(post.getTitle(), post.getPermalink())
                            .setFooter("Posted in r/" + post.getSubreddit() + " by u/" + post.getAuthor());
                    event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                    event.reply(post.getUrl()).queue();
                } else {
                    embedBuilder.setTitle(post.getTitle(), post.getPermalink())
                            .setImage(post.getUrl())
                            .setFooter("Posted in r/" + post.getSubreddit() + " by u/" + post.getAuthor());
                    event.replyEmbeds(embedBuilder.build()).queue();
                }
            }
        }
    }
}
