package com.discord.bot.commands.redditcommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Post;
import com.discord.bot.service.PostService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class MemesCommand implements ISlashCommand {
    PostService postService;
    RedditCommandUtils utils;

    public MemesCommand(PostService postService, RedditCommandUtils utils) {
        this.postService = postService;
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<Post> list = postService.getPosts("memes");
        utils.checkTypeAndPost(event, list);
        net.dv8tion.jda.api.entities.User user = event.getUser();
        utils.counter(user.getId(), user.getAsTag());
    }
}
