package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.YoutubeVideo;
import com.discord.bot.service.YoutubeVideoService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

@AllArgsConstructor
public class AddYoutubeVideoCommand implements ISlashCommand {
    final YoutubeVideoService youtubeVideoService;
    private final String adminUserId;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(adminUserId)) {
            var urlOption = event.getOption("url");
            var genreOption = event.getOption("genre");

            if (urlOption == null || genreOption == null) return;

            String url = urlOption.getAsString();
            String genre = genreOption.getAsString();

            var video = youtubeVideoService.getYoutubeVideo(url);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            if (video != null) {
                embedBuilder.setDescription("Video already exists in the database.")
                        .setColor(Color.RED);
            } else {
                YoutubeVideo youtubeVideo = new YoutubeVideo(url, genre);

                youtubeVideoService.save(youtubeVideo);

                embedBuilder.setDescription("Video added to the database.")
                        .setColor(Color.GREEN);
            }

            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }
}
