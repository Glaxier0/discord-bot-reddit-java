package com.discord.bot.Event;

import com.discord.bot.Entity.Todo;
import com.discord.bot.Service.TodoService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

public class ToDoCommands extends ListenerAdapter {

    TodoService service;

    public ToDoCommands(TodoService service) {
        this.service = service;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
        boolean isBot = Objects.requireNonNull(event.getMember()).getUser().isBot();
        String userWithTag = Objects.requireNonNull(event.getMember()).getUser().getAsTag();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (messageSent.startsWith("!todo add") && !isBot) {
            String add = messageSent.replaceAll("\\s+", " ").substring(9);
            if (add.isEmpty()) {
                String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                embedBuilder.setDescription(user + " Can't add your future to to-do list.");
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            } else if (add.length() < 213) {
                if (service.todoList(userWithTag).size() < 30) {
                    Date createdDate = java.sql.Date.valueOf(event.getMessage().getTimeCreated().toLocalDate());
                    service.save(new Todo(userWithTag, add, createdDate));
                    embedBuilder.setDescription("Successfully added to your to-do list");
                } else {
                    String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                    embedBuilder.setDescription(user + " To-do list limit is 30 row." +
                            "\nPlease remove some rows before adding another one.");
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            } else {
                String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                embedBuilder.setDescription(user + " to-do row must be less than 213 characters");
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }

        if (messageSent.equalsIgnoreCase("!todo list") && !isBot) {
            List<Todo> todoList = service.todoList(userWithTag);
            if (todoList.isEmpty()) {
                String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                embedBuilder.setDescription(user + " Your to-do list is empty as my future!");
            } else {
                int i = 1;
                embedBuilder.setAuthor(userWithTag)
                        .setDescription("__**``ID   DATE    TODO``**__\n");
                for (Todo todoRow : todoList) {
                    embedBuilder.appendDescription(i + ". " + todoRow.getCreated().toLocalDate()
                            + " " + todoRow.getTodoRow() + "\n");
                    i++;
                }
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if (messageSent.startsWith("!todo remove")) {
            String remove = messageSent.replaceAll("\\s+", " ").substring(12);
            if (remove.isEmpty()) {
                String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                embedBuilder.setDescription(user + " Please write something after !todo remove command." +
                        "\ne.g. !todo remove 1");
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            } else {
                try {
                    int rowId = Integer.parseInt(remove.substring(1));
                    List<Todo> todoList = service.todoList(userWithTag);
                    if (rowId > 0 && rowId <= todoList.size()) {
                        service.delete(todoList.get(rowId - 1));
                        embedBuilder.setDescription(rowId + ". row of to-do list deleted");
                    } else {
                        embedBuilder.setDescription("Please enter a viable row id of your to-do list without '.'");
                    }
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                } catch (NumberFormatException e) {
                    embedBuilder.setDescription("Please enter numeric id of to-do row.");
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }
            }
        }
    }
}
