package com.discord.bot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class TestCommands {
    public void addTestCommands(JDA jda, String TEST_SERVER) {
        while (jda.getGuildById(TEST_SERVER) == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Guild testServer = jda.getGuildById(TEST_SERVER);
        CommandListUpdateAction testServerCommands = testServer.updateCommands();

        testServerCommands.addCommands(
                //admin commands
                Commands.slash("guilds", "Get guild list that bot is in."),
                Commands.slash("status", "Get reddit post statuses."),
                Commands.slash("stats", "Get user stats.")
                        .addOptions(new OptionData(OptionType.MENTIONABLE, "user", "User with mention.")
                                .setRequired(true)),
                Commands.slash("users", "Get bot users."),
                Commands.slash("logs", "Get logs.")
        ).queue();
    }
}
