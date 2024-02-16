package com.discord.bot.commands.todocommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.service.TodoService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

@AllArgsConstructor
public class ToDoClearCommand implements ISlashCommand {
    ToDoCommandUtils utils;
    TodoService todoService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        String userId = user.getId();
        String userWithTag = user.getAsTag();

        todoService.deleteAll(userId);
        event.replyEmbeds(new EmbedBuilder().setDescription("To-do list cleared.").setColor(Color.GREEN).build()).queue();

        utils.counter(userId, userWithTag);
    }
}
