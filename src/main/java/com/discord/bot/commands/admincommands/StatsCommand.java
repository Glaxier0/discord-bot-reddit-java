package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.Entity.User;
import com.discord.bot.Service.UserService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

@AllArgsConstructor
public class StatsCommand implements ISlashCommand {
    final UserService userService;
    private final String adminUserId;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(adminUserId)) {
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
