package com.discord.bot.commands.todocommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Todo;
import com.discord.bot.service.TodoService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.sql.Date;
import java.time.LocalDateTime;

public class ToDoAddCommand implements ISlashCommand {
    ToDoCommandUtils utils;
    TodoService todoService;

    public ToDoAddCommand(ToDoCommandUtils utils, TodoService todoService) {
        this.utils = utils;
        this.todoService = todoService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        String userId = user.getId();
        String userWithTag = user.getAsTag();
        String add = event.getOption("task").getAsString();

        if (add.isEmpty()) {
            embedBuilder.setDescription("Can't add your future to to-do list.");
        } else if (add.length() < 213) {
            if (todoService.todoList(userId).size() < 30) {
                Date createdDate = Date.valueOf(LocalDateTime.now().toLocalDate());
                todoService.save(new Todo(userId, add, createdDate, false, userWithTag));
                embedBuilder.setDescription("Successfully added to your to-do list").setColor(Color.GREEN);
            } else {
                embedBuilder.setDescription("To-do list limit is 30 row." +
                        "\nPlease remove some rows before adding another one.").setColor(Color.RED);
            }
        } else {
            embedBuilder.setDescription("To-do row must be less than 213 characters.").setColor(Color.RED);
        }
        event.replyEmbeds(embedBuilder.build()).queue();

        utils.counter(userId, userWithTag);
    }
}
