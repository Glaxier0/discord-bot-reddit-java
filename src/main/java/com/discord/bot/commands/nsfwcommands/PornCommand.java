package com.discord.bot.commands.nsfwcommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Post;
import com.discord.bot.service.PostService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class PornCommand implements ISlashCommand {
    PostService postService;
    NsfwCommandUtils utils;

    Random random = new Random();

    public PornCommand(PostService postService, NsfwCommandUtils utils) {
        this.postService = postService;
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getTextChannel().isNSFW()) {
            List<Post> postList = postService.getPorn();
            Post post = postList.get(random.nextInt(postList.size()));
            utils.checkTypeAndPost(event, post);
            net.dv8tion.jda.api.entities.User user = event.getUser();
            utils.counter(user.getId(), user.getAsTag(), true);
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Channel is not nsfw. Please write command in nsfw channel.")
                    .setColor(Color.RED).build()).queue();
        }
    }
}
