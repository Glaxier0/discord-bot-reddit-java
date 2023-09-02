package com.discord.bot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.Objects;

public class TestCommands {
    public void addTestCommands(JDA jda, String TEST_SERVER) {
        while (jda.getGuildById(TEST_SERVER) == null) {
            try {
                //noinspection BusyWait
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Guild testServer = jda.getGuildById(TEST_SERVER);
        CommandListUpdateAction testServerCommands = Objects.requireNonNull(testServer).updateCommands();

        testServerCommands.addCommands(
                //Admin Commands
                Commands.slash("guilds", "Get guild list that bot is in."),
                Commands.slash("status", "Get reddit post statuses."),
                Commands.slash("stats", "Get user stats.")
                        .addOptions(new OptionData(OptionType.MENTIONABLE, "user", "User with mention.")
                                .setRequired(true)),
                Commands.slash("users", "Get bot users."),
                Commands.slash("logs", "Get logs."),
                //Reddit Admin Commands
                Commands.slash("add", "Add a subreddit.").addOptions(
                        new OptionData(OptionType.STRING, "name", "Name of the subreddit.", true),
                        new OptionData(OptionType.STRING, "genre", "Sub genre of the subreddit " +
                                "(reddit, porn, tits or hentai).", true),
                        new OptionData(OptionType.BOOLEAN, "nsfw", "Is subreddit nsfw?", true)),
                Commands.slash("list", "List subreddits."),
                Commands.slash("delete", "Delete a subreddit.")
                        .addOption(OptionType.STRING, "name", "Name of the subreddit.", true)
        ).queue();
    }
}
