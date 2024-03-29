package com.discord.bot.commands;

import com.discord.bot.service.SubredditService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.List;

@AllArgsConstructor
public class JdaCommands {
    final SubredditService subredditService;
    public void addJdaCommands(JDA jda) {
        CommandListUpdateAction globalCommands = jda.updateCommands();

        //Reddit Commands
        commandBuilder(globalCommands);

        globalCommands.addCommands(
                //NSFW Commands
                Commands.slash("nhelp", "Info page about bot nsfw commands").setNSFW(true),
                Commands.slash("hentai", "Get random hentai image/gif/video.").setNSFW(true),
                Commands.slash("porn", "Get random porn image/gif/video.").setNSFW(true),
                Commands.slash("tits", "Get random tits image/gif/video.").setNSFW(true),
                Commands.slash("redgifs", "Get random trending gif of chosen tag").setNSFW(true)
                        .addOptions(new OptionData(OptionType.STRING, "sort", "Sort by popularity")
                                        .addChoices(new Command.Choice("Trending", 1),
                                                new Command.Choice("Top", 2))
                                        .setRequired(true),
                                new OptionData(OptionType.STRING, "tag", "Chosen tag or a.k.a category")
                                        .setRequired(true)
                        ),
                //Text Commands
                Commands.slash("help", "Info page about bot commands"),
                Commands.slash("monke", "Get my favorite random monke video."),
                Commands.slash("github", "My github page and source code of bot."),
                Commands.slash("howgay", "Calculate how gay is someone.")
                        .addOptions(new OptionData(OptionType.USER, "user", "User to calculate how gay.")
                                .setRequired(true)),
                Commands.slash("errrkek", "Calculate how man is someone.")
                        .addOptions(new OptionData(OptionType.USER, "user", "User to calculate how man.")
                                .setRequired(true)),
                Commands.slash("topgg", "Top.gg page of Glaxier bot."),
                //To-do Commands
                Commands.slash("todoadd", "Add a task to your to-do list.")
                        .addOptions(new OptionData(OptionType.STRING, "task", "A to-do task.")
                                .setRequired(true)),
                Commands.slash("todolist", "Shows your to-do list."),
                Commands.slash("todoremove", "Remove a task from your to-do list.")
                        .addOptions(new OptionData(OptionType.INTEGER, "taskid", "To-do task id to remove.")
                                .setRequired(true)),
                Commands.slash("todoupdate", "Update a task in your to-do list.")
                        .addOptions(new OptionData(OptionType.INTEGER, "taskid", "To-do task id to remove.")
                                        .setRequired(true),
                                new OptionData(OptionType.STRING, "task",
                                        "Updated to-do task.")
                                        .setRequired(true)),
                Commands.slash("todocomplete", "Complete a task in your to-do list.")
                        .addOptions(new OptionData(OptionType.INTEGER, "taskid", "To-do task id to remove.")
                                .setRequired(true)),
                Commands.slash("todoclear", "Clears your to-do list.")
        ).queue();
    }

    private void commandBuilder(CommandListUpdateAction commandListUpdateAction) {
        List<String> subreddits = subredditService.getSubredditsByGenre("reddit");

        for (String subreddit : subreddits) {
            String commandName = subreddit.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
            String description = "Get top r/" + subreddit + " posts.";

            //noinspection ResultOfMethodCallIgnored
            commandListUpdateAction.addCommands(Commands.slash(commandName, description));
        }
    }
}