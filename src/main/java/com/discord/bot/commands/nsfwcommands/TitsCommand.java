package com.discord.bot.commands.nsfwcommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Post;
import com.discord.bot.service.PostService;
import com.discord.bot.service.SubredditService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;
import java.util.Random;

@AllArgsConstructor
public class TitsCommand implements ISlashCommand {
    PostService postService;
    SubredditService subredditService;
    NsfwCommandUtils utils;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<String> subreddits = subredditService.getSubredditsByGenre("tits");
        List<Post> postList = postService.getBySubreddits(subreddits);
        Post post = postList.get(new Random().nextInt(postList.size()));
        utils.checkTypeAndPost(event, post);
        net.dv8tion.jda.api.entities.User user = event.getUser();
        utils.counter(user.getId(), user.getAsTag(), true);
    }
}
