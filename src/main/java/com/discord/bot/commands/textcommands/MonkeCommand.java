package com.discord.bot.commands.textcommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.service.YoutubeVideoService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Random;

public class MonkeCommand implements ISlashCommand {
    final YoutubeVideoService youtubeVideoService;
    final TextCommandUtils utils;

    public MonkeCommand(YoutubeVideoService youtubeVideoService, TextCommandUtils utils) {
        this.youtubeVideoService = youtubeVideoService;
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        var monkeVideos = youtubeVideoService.getYoutubeVideosByGenre("monke");
        event.reply(monkeVideos.get(new Random().nextInt(monkeVideos.size()))).queue();

        net.dv8tion.jda.api.entities.User user = event.getUser();
        utils.counter(user.getId(), user.getName());
    }
}