package com.discord.bot.commands.nsfwcommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Post;
import com.discord.bot.service.PostService;
import com.discord.bot.service.SubredditService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;
import java.util.Random;

@AllArgsConstructor
public class PornCommand implements ISlashCommand {
    PostService postService;
    SubredditService subredditService;
    NsfwCommandUtils utils;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<String> subreddits = subredditService.getSubredditsByGenre("porn");
        List<Post> postList = postService.getBySubreddits(subreddits);
        Post post = postList.get(new Random().nextInt(postList.size()));
        utils.checkTypeAndPost(event, post);
        User user = event.getUser();
        utils.counter(user.getId(), user.getName(), true);
    }
}
