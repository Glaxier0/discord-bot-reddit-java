package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

public class StatsCommand implements ISlashCommand {
    UserService userService;
    String ADMIN = "your_discord_id";

    public StatsCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            net.dv8tion.jda.api.entities.User userStats = Objects.requireNonNull(event.getOption("user"))
                    .getAsUser();

            User user = userService.getUser(userStats.getId());
            if (user == null) {
                embedBuilder.setDescription("No data found on user " + userStats.getAsMention());
            } else {
                embedBuilder.setDescription("User: " + userStats.getAsMention() +
                        "\nText commands: " + user.getTextCount() +
                        "\nHentai commands: " + user.getHCount() +
                        "\nPorn commands: " + user.getPCount() +
                        "\nReddit commands: " + user.getRedditCount() +
                        "\nTodo commands: " + user.getTodoCount());
            }
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }
}
