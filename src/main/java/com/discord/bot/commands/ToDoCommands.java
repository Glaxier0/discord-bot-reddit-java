package com.discord.bot.commands;

import com.discord.bot.entity.Todo;
import com.discord.bot.entity.User;
import com.discord.bot.service.TodoService;
import com.discord.bot.service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public class ToDoCommands extends ListenerAdapter {

    TodoService todoService;
    UserService userService;

    public ToDoCommands(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "todoadd" -> todoAdd(event);
            case "todolist" -> todoList(event);
            case "todoremove" -> todoRemove(event);
            case "todoupdate" -> todoUpdate(event);
            case "todocomplete" -> todoComplete(event);
            case "todoclear" -> todoClear(event);
        }
    }

    private void todoAdd(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        String userId = user.getId();
        String userWithTag = user.getAsTag();
        counter(userId, userWithTag);

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
    }

    private void todoList(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        String userId = user.getId();
        String userWithTag = user.getAsTag();
        counter(userId, userWithTag);

        List<Todo> todoList = todoService.todoList(userId);
        if (todoList.isEmpty()) {
            embedBuilder.setDescription("Your to-do list is empty as my future!");
        } else {
            int i = 1;
            embedBuilder.setAuthor(userWithTag)
                    .setDescription("__**``   ID   DATE    TODO``**__\n");
            for (Todo todoRow : todoList) {
                String complete = (i + ". " + todoRow.getCreated().toLocalDate()
                        + " " + todoRow.getTodoRow() + "\n");
                if (todoRow.isCompleted()) {

                    embedBuilder.appendDescription(":white_check_mark: " + complete);
                } else {
                    embedBuilder.appendDescription(":x: " + complete);
                }
                i++;
            }
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void todoRemove(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        String userId = user.getId();
        String userWithTag = user.getAsTag();
        counter(userId, userWithTag);

        int rowId = event.getOption("taskid").getAsInt();
        List<Todo> todoList = todoService.todoList(userId);

        if (rowId > 0 && rowId <= todoList.size()) {
            todoService.delete(todoList.get(rowId - 1));
            embedBuilder.setDescription(rowId + ". row of to-do list deleted!").setColor(Color.GREEN);
        } else {
            embedBuilder.setDescription("Invalid row id " + rowId).setColor(Color.RED);
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void todoUpdate(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        String userId = user.getId();
        String userWithTag = user.getAsTag();
        counter(userId, userWithTag);

        int rowId = event.getOption("taskid").getAsInt();
        String updateTask = event.getOption("task").getAsString();

        List<Todo> todoList = todoService.todoList(userId);
        if (rowId > 0 && rowId <= todoList.size()) {
            todoList.get(rowId - 1).setTodoRow(updateTask);
            todoService.save(todoList.get(rowId - 1));
            embedBuilder.setDescription(rowId + ". row of to-do list updated!").setColor(Color.GREEN);
        } else {
            embedBuilder.setDescription("Invalid row id " + rowId).setColor(Color.RED);
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void todoComplete(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        String userId = user.getId();
        String userWithTag = user.getAsTag();
        counter(userId, userWithTag);

        int rowId = event.getOption("taskid").getAsInt();
        List<Todo> todoList = todoService.todoList(userId);
        if (rowId > 0 && rowId <= todoList.size()) {
            todoList.get(rowId - 1).setCompleted(true);
            todoService.save(todoList.get(rowId - 1));
            embedBuilder.setDescription(rowId + ". row of to-do list completed!").setColor(Color.GREEN);
        } else {
            embedBuilder.setDescription("Invalid row id " + rowId).setColor(Color.RED);
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void todoClear(SlashCommandInteractionEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getUser();
        String userId = user.getId();
        String userWithTag = user.getAsTag();
        counter(userId, userWithTag);

        todoService.deleteAll(userId);
        event.replyEmbeds(new EmbedBuilder().setDescription("To-do list cleared.").setColor(Color.GREEN).build()).queue();
    }

    private void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag, 0);
        }
        user.setTodoCount(user.getTodoCount() + 1);
        userService.save(user);
    }
}
