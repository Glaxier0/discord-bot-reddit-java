package com.discord.bot.Event;

import com.discord.bot.Entity.Post;
import com.discord.bot.Entity.User;
import com.discord.bot.Service.PostService;
import com.discord.bot.Service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.*;

public class NsfwCommands extends ListenerAdapter {

    PostService postService;
    UserService userService;

    public NsfwCommands(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    Random random = new Random();
    List<String> subreddit = Arrays.asList("!hentai", "!porn");

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String messageSent = event.getMessage().getContentRaw();
        boolean isBot = event.getMember().getUser().isBot();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (subreddit.contains(messageSent.toLowerCase()) && !isBot) {
            String userId = Objects.requireNonNull(event.getMember()).getUser().getId();
            String userWithTag = Objects.requireNonNull(event.getMember()).getUser().getAsTag();
            User user = userService.getUser(userId);
            if (user == null) {
                user = new User(userId, 0, 0, 0, 0, 0, userWithTag);
            }
            if (event.getChannel().isNSFW()) {
                List<Post> postList;
                if (messageSent.equalsIgnoreCase("!hentai")) {
                    postList = postService.getHentai();
                    user.setHCount(user.getHCount() + 1);
                } else {
                    postList = postService.getPorn();
                    user.setPCount(user.getPCount() + 1);
                }
                userService.save(user);

                Post post = postList.get(random.nextInt(postList.size()));

                if (post.getUrl().contains("redgifs.com")
                        || (post.getUrl().contains("imgur.com") && post.getUrl().contains(".gifv"))
                        || post.getUrl().contains("gfycat.com")) {
                    post.setContentType("video");
                }

                switch (post.getContentType()) {
                    case "video" -> event.getChannel().sendMessage(post.getUrl()).queue();
                    case "image" -> {
                        embedBuilder.setTitle(post.getTitle(), post.getPermaUrl())
                                .setImage(post.getUrl())
                                .setFooter("Posted in r/" + post.getSubreddit() + " by u/" + post.getAuthor());
                        event.getChannel().sendMessage(embedBuilder.build()).queue();
                    }
                    case "text" -> event.getChannel().sendMessage(post.getPermaUrl()).queue();
                }
            } else {
                    embedBuilder.setDescription("Channel is not nsfw. Please write command in nsfw Channel");
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
