package com.discord.bot.Event;

import com.discord.bot.Entity.Post;
import com.discord.bot.Service.PostService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RedditCommands extends ListenerAdapter {

    PostService service;

    public RedditCommands(PostService service) {
        this.service = service;
    }

    Random random = new Random();
    List<String> subreddit = Arrays.asList("!unexpected", "!dankmemes", "!memes", "!greentext");

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String messageSent = event.getMessage().getContentRaw();
        boolean isBot = Objects.requireNonNull(event.getMember()).getUser().isBot();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (subreddit.contains(messageSent.toLowerCase()) && !isBot) {

            String subreddit = messageSent.toLowerCase().substring(1);

            if(subreddit.equals("unexpected")) {
                subreddit = "Unexpected";
            }

            List<Post> list = service.getPosts(subreddit);
            Post post = list.get(random.nextInt(list.size()));

            switch (post.getContentType()) {
                case "video" -> {
                    while (post.getVimeoUrl() == null && (post.getContentType().equals("video"))) {
                        post = list.get(random.nextInt(list.size()));
                    }
                    event.getChannel().sendMessage(post.getVimeoUrl()).queue();
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

