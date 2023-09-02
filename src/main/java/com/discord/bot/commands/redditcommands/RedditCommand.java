package com.discord.bot.commands.redditcommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Post;
import com.discord.bot.service.PostService;
import com.discord.bot.service.SubredditService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class RedditCommand implements ISlashCommand {
    PostService postService;
    SubredditService subredditService;
    RedditCommandUtils utils;

    public RedditCommand(PostService postService, SubredditService subredditService, RedditCommandUtils utils) {
        this.postService = postService;
        this.subredditService = subredditService;
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String subreddit = event.getFullCommandName();
        List<Post> list = postService.getPosts(subreddit);
        utils.checkTypeAndPost(event, list);
        net.dv8tion.jda.api.entities.User user = event.getUser();
        utils.counter(user.getId(), user.getAsTag());
    }
}
