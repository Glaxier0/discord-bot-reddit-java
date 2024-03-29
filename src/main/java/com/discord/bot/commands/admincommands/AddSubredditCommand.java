package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Subreddit;
import com.discord.bot.service.SubredditService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

@AllArgsConstructor
public class AddSubredditCommand implements ISlashCommand {
    final SubredditService subredditService;
    private final String adminUserId;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(adminUserId)) {
            var nameOption = event.getOption("name");
            var genreOption = event.getOption("genre");
            var nsfwOption = event.getOption("nsfw");

            if (nameOption == null || genreOption == null || nsfwOption == null) return;

            String name = nameOption.getAsString();
            String genre = genreOption.getAsString();
            boolean nsfw = nsfwOption.getAsBoolean();

            Subreddit subreddit = new Subreddit(name, genre, nsfw);

            EmbedBuilder embedBuilder = new EmbedBuilder();

            subredditService.save(subreddit);

            embedBuilder.setDescription("Subreddit: " + name + " added to the database.")
                    .setColor(Color.GREEN);
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }
}