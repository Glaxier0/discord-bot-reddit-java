package com.discord.bot.commands.todocommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Todo;
import com.discord.bot.service.TodoService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

@AllArgsConstructor
public class ToDoListCommand implements ISlashCommand {
    ToDoCommandUtils utils;
    TodoService todoService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        User user = event.getUser();
        String userId = user.getId();
        String userName = user.getName();

        List<Todo> todoList = todoService.todoList(userId);
        if (todoList.isEmpty()) {
            embedBuilder.setDescription("Your to-do list is empty as my future!");
        } else {
            int i = 1;
            embedBuilder.setAuthor(userName)
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
        utils.counter(userId, userName);
    }
}
