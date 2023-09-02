package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Subreddit;
import com.discord.bot.service.SubredditService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class DeleteSubCommand implements ISlashCommand {
    SubredditService subredditService;

    String ADMIN = "your_discord_id";

    public DeleteSubCommand(SubredditService subredditService) {
        this.subredditService = subredditService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            var nameOption = event.getOption("name");

            if (nameOption == null) {
                return;
            }

            String name = nameOption.getAsString().trim();

            if (event.getUser().getId().equals(ADMIN)) {
                EmbedBuilder embedBuilder = new EmbedBuilder();

                Subreddit subreddit = subredditService.getSubreddit(name);

                if (subreddit == null) {
                    embedBuilder.setDescription("Subreddit: " + name + " not found.")
                            .setColor(Color.RED);
                } else {
                    subredditService.delete(subreddit);

                    embedBuilder.setDescription("Subreddit: " + name + " deleted from the database.")
                            .setColor(Color.GREEN);
                }

                event.replyEmbeds(embedBuilder.build()).queue();
            }
        }
    }
}
