package com.discord.bot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminCommands {
    private static final Logger logger = LoggerFactory.getLogger(AdminCommands.class);

    public void addAdminCommands(JDA jda, String adminServerId) {
        Guild adminServer = jda.getGuildById(adminServerId);

        if (adminServer == null) {
            logger.error("Could not find the server with id: " + adminServerId);
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
                Commands.slash("addvideo", "Add a video.")
                        .addOptions(new OptionData(OptionType.STRING, "url", "Url of the video.")
                                        .setRequired(true),
                                new OptionData(OptionType.STRING, "genre", "Genre of the video " +
                                        "(monke etc).").setRequired(true)),
                Commands.slash("deletevideo", "Delete a video.")
                        .addOptions(new OptionData(OptionType.STRING, "url", "Url of the video.")
                                .setRequired(true)),
                //Custom Commands
                Commands.slash("formylove", "For the special one.")
        ).queue();
    }
}
