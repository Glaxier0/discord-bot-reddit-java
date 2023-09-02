package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Subreddit;
import com.discord.bot.service.SubredditService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class AddSubCommand implements ISlashCommand {
    SubredditService subredditService;

    public AddSubCommand(SubredditService subredditService) {
        this.subredditService = subredditService;
    }

    String ADMIN = "315403352496275456";

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        var nameOption = event.getOption("name");
        var genreOption = event.getOption("genre");
        var nsfwOption = event.getOption("nsfw");

        if (nameOption == null || genreOption == null || nsfwOption == null) {
            return;
        }

        String name = nameOption.getAsString().trim();
        String genre = genreOption.getAsString().trim();
        boolean nsfw = nsfwOption.getAsBoolean();

        Subreddit subreddit = new Subreddit(name, genre, nsfw);

        if (event.getUser().getId().equals(ADMIN)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            subredditService.save(subreddit);

            embedBuilder.setDescription("Subreddit: " + name + " added to the database.")
                    .setColor(Color.GREEN);
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }
}
