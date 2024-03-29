package com.discord.bot.commands.todocommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Todo;
import com.discord.bot.service.TodoService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class ToDoUpdateCommand implements ISlashCommand {
    ToDoCommandUtils utils;
    TodoService todoService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        User user = event.getUser();
        String userId = user.getId();

        int rowId = Objects.requireNonNull(event.getOption("taskid")).getAsInt();
        String updateTask = Objects.requireNonNull(event.getOption("task")).getAsString();

        List<Todo> todoList = todoService.todoList(userId);
        if (rowId > 0 && rowId <= todoList.size()) {
            todoList.get(rowId - 1).setTodoRow(updateTask);
            todoService.save(todoList.get(rowId - 1));
            embedBuilder.setDescription(rowId + ". row of to-do list updated!").setColor(Color.GREEN);
        } else {
            embedBuilder.setDescription("Invalid row id " + rowId).setColor(Color.RED);
        }
        event.replyEmbeds(embedBuilder.build()).queue();

        utils.counter(userId, user.getName());
    }
}
