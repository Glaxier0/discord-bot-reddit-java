package com.discord.bot.commands.nsfwcommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Post;
import com.discord.bot.service.PostService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class HentaiCommand implements ISlashCommand {
    PostService postService;
    NsfwCommandUtils utils;

    Random random = new Random();

    public HentaiCommand(PostService postService, NsfwCommandUtils utils) {
        this.postService = postService;
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getTextChannel().isNSFW()) {
            List<Post> postList = postService.getHentai();
            Post post = postList.get(random.nextInt(postList.size()));
            utils.checkTypeAndPost(event, post);
            net.dv8tion.jda.api.entities.User user = event.getUser();
            utils.counter(user.getId(), user.getAsTag(), false);
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Channel is not nsfw.")
                    .setColor(Color.RED).build()).queue();
        }
    }
}
