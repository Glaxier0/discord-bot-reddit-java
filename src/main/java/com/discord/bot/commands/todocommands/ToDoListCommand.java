package com.discord.bot.commands.todocommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Todo;
import com.discord.bot.service.TodoService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class ToDoListCommand implements ISlashCommand {
    ToDoCommandUtils utils;
    TodoService todoService;

    public ToDoListCommand(ToDoCommandUtils utils, TodoService todoService) {
        this.utils = utils;
        this.todoService = todoService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        String userId = user.getId();
        String userWithTag = user.getAsTag();

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
        utils.counter(userId, userWithTag);
    }
}
