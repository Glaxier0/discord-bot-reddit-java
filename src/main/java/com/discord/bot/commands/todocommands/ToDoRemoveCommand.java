package com.discord.bot.commands.todocommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Todo;
import com.discord.bot.service.TodoService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;

public class ToDoRemoveCommand implements ISlashCommand {
    ToDoCommandUtils utils;
    TodoService todoService;

    public ToDoRemoveCommand(ToDoCommandUtils utils, TodoService todoService) {
        this.utils = utils;
        this.todoService = todoService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        String userId = user.getId();
        String userWithTag = user.getAsTag();

        int rowId = event.getOption("taskid").getAsInt();
        List<Todo> todoList = todoService.todoList(userId);

        if (rowId > 0 && rowId <= todoList.size()) {
            todoService.delete(todoList.get(rowId - 1));
            embedBuilder.setDescription(rowId + ". row of to-do list deleted!").setColor(Color.GREEN);
        } else {
            embedBuilder.setDescription("Invalid row id " + rowId).setColor(Color.RED);
        }
        event.replyEmbeds(embedBuilder.build()).queue();

        utils.counter(userId, userWithTag);
    }
}
