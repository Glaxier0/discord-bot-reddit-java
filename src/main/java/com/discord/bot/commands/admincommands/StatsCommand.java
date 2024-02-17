package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@AllArgsConstructor
public class StatsCommand implements ISlashCommand {
    final UserService userService;
    private final String adminUserId;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(adminUserId)) {
            var userOption = event.getOption("user");
            if (userOption == null) return;

            String userId = userOption.getAsString();
            EmbedBuilder embedBuilder = new EmbedBuilder();

            User user = userService.getUser(userId);
            if (user == null) {
                embedBuilder.setDescription("No data found on user <@" + userId + ">");
            } else {
                embedBuilder.setDescription("User: <@" + userId + ">" +
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
