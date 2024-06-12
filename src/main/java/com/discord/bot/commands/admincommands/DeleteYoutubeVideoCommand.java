package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.YoutubeVideo;
import com.discord.bot.service.YoutubeVideoService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

@AllArgsConstructor
public class DeleteYoutubeVideoCommand implements ISlashCommand {
    final YoutubeVideoService youtubeVideoService;
    private final String adminUserId;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(adminUserId)) {
            var urlOption = event.getOption("url");

            if (urlOption == null) return;

            String url = urlOption.getAsString();

            EmbedBuilder embedBuilder = new EmbedBuilder();

            YoutubeVideo youtubeVideo = youtubeVideoService.getYoutubeVideo(url);

            if (youtubeVideo == null) {
                embedBuilder.setDescription("Video: " + url + " not found.")
                        .setColor(Color.RED);
            } else {
                youtubeVideoService.delete(youtubeVideo);

                embedBuilder.setDescription("Video: " + url + " deleted from the database.")
                        .setColor(Color.GREEN);
            }

            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }
}