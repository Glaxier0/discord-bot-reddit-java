package com.discord.bot.Event;

import com.discord.bot.Entity.Post;
import com.discord.bot.Entity.User;
import com.discord.bot.Service.PostService;
import com.discord.bot.Service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RedditCommands extends ListenerAdapter {

    PostService postService;
    UserService userService;
    Random random = new Random();
    List<String> subreddit = Arrays.asList("!unexpected", "!dankmemes", "!memes", "!greentext");

    public RedditCommands(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String messageSent = event.getMessage().getContentRaw();
        boolean isBot = Objects.requireNonNull(event.getMember()).getUser().isBot();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (subreddit.contains(messageSent.toLowerCase()) && !isBot) {
            String userId = Objects.requireNonNull(event.getMember()).getUser().getId();
            String userWithTag = event.getMember().getUser().getAsTag();
            User user = userService.getUser(userId);
            if (user == null) {
                user = new User(userId, 0, 0, 0, 0, 0, userWithTag);
            }
            user.setRedditCount(user.getRedditCount() + 1);
            userService.save(user);
            String subreddit = messageSent.toLowerCase().substring(1);
            if (subreddit.equals("unexpected")) {
                subreddit = "Unexpected";
            }

            List<Post> list = postService.getPosts(subreddit);
            Post post = list.get(random.nextInt(list.size()));

            switch (post.getContentType()) {
                case "video" -> {
                    while (post.getFirebaseUrl() == null && (post.getContentType().equals("video"))) {
                        post = list.get(random.nextInt(list.size()));
                    }
                    embedBuilder.setTitle(post.getTitle(), post.getPermaUrl())
                            .setFooter("Posted in r/" + post.getSubreddit() + " by u/" + post.getAuthor());
                    event.getChannel().sendMessage(post.getFirebaseUrl()).queue();
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }
                case "gif", "image" -> {
                    embedBuilder.setTitle(post.getTitle(), post.getPermaUrl())
                            .setImage(post.getUrl())
                            .setFooter("Posted in r/" + post.getSubreddit() + " by u/" + post.getAuthor());
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }
                case "text" -> event.getChannel().sendMessage(post.getPermaUrl()).queue();
            }
        }
    }
}

