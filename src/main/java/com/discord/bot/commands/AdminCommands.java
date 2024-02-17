package com.discord.bot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class AdminCommands {
    public void addAdminCommands(JDA jda, String adminServerId) {
        Guild adminServer = jda.getGuildById(adminServerId);

        if (adminServer == null) {
            System.out.println("Could not find the server with id: " + adminServerId);
            return;
        }


        CommandListUpdateAction adminServerCommands = adminServer.updateCommands();

        adminServerCommands.addCommands(
                //Admin Commands
                Commands.slash("guilds", "Get guild list that bot is in."),
                Commands.slash("status", "Get reddit post statuses."),
                Commands.slash("stats", "Get user stats.")
                        .addOptions(new OptionData(OptionType.STRING, "user", "User id.")
                                .setRequired(true)),
                Commands.slash("users", "Get bot users."),
                Commands.slash("logs", "Get logs."),
                //Reddit Admin Commands
                Commands.slash("add", "Add a subreddit.")
                        .addOptions(new OptionData(OptionType.STRING, "name", "Name of the subreddit.")
                                        .setRequired(true),
                                new OptionData(OptionType.STRING, "genre", "Sub genre of the subreddit " +
                                        "(reddit, porn, tits or hentai).").setRequired(true),
                                new OptionData(OptionType.BOOLEAN, "nsfw", "Is subreddit nsfw?")
                                        .setRequired(true)),
                Commands.slash("list", "List subreddits."),
                Commands.slash("delete", "Delete a subreddit.")
                        .addOptions(new OptionData(OptionType.STRING, "name", "Name of the subreddit.")
                                .setRequired(true)),
                //Custom Commands
                Commands.slash("formylove", "For the special one.")
        ).queue();
    }
}
