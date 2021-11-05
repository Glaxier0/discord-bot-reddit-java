package com.discord.bot.Event;

import com.discord.bot.Entity.Todo;
import com.discord.bot.Entity.User;
import com.discord.bot.Service.TodoService;
import com.discord.bot.Service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ToDoCommands extends ListenerAdapter {

    TodoService todoService;
    UserService userService;

    public ToDoCommands(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
        boolean isBot = Objects.requireNonNull(event.getMember()).getUser().isBot();
        String userId = Objects.requireNonNull(event.getMember()).getUser().getId();
        String userWithTag = Objects.requireNonNull(event.getMember()).getUser().getAsTag();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (messageSent.startsWith("!todo add") && !isBot) {
            counter(userId, userWithTag);
            String add = messageSent.replaceAll("\\s+", " ").substring(9);
            if (add.isEmpty()) {
                String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                embedBuilder.setDescription(user + " Can't add your future to to-do list.");
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            } else if (add.length() < 213) {
                if (todoService.todoList(userId).size() < 30) {
                    Date createdDate = java.sql.Date.valueOf(event.getMessage().getTimeCreated().toLocalDate());
                    todoService.save(new Todo(userId, add, createdDate, false, userWithTag));
                    embedBuilder.setDescription("Successfully added to your to-do list").setColor(Color.GREEN);
                } else {
                    String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                    embedBuilder.setDescription(user + " To-do list limit is 30 row." +
                            "\nPlease remove some rows before adding another one.").setColor(Color.RED);
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            } else {
                String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                embedBuilder.setDescription(user + " to-do row must be less than 213 characters").setColor(Color.RED);
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }

        if (messageSent.equalsIgnoreCase("!todo list") && !isBot) {
            counter(userId, userWithTag);
            List<Todo> todoList = todoService.todoList(userId);
            if (todoList.isEmpty()) {
                String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                embedBuilder.setDescription(user + " Your to-do list is empty as my future!");
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
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if (messageSent.startsWith("!todo remove")) {
            counter(userId, userWithTag);
            String remove = messageSent.replaceAll("\\s+", " ").substring(12);

            if (remove.isEmpty()) {
                String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                embedBuilder.setDescription(user + " Please write something after !todo remove command." +
                        "\ne.g. !todo remove 1").setColor(Color.RED);
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            } else {
                if (remove.startsWith(" ")) {
                    remove = remove.substring(1);
                }
                List<String> stringList = List.of(remove.split("\\s"));
                List<Todo> todoList = todoService.todoList(userId);
                List<Integer> removeList = new ArrayList<>();
                List<Integer> wrongList = new ArrayList<>();
                int catchCounter = 0;
                for (String removeRow : stringList) {
                    try {
                        int rowId = Integer.parseInt(removeRow);
                        if (rowId > 0 && rowId <= todoList.size()) {
                            removeList.add(rowId);
                        } else {
                            wrongList.add(rowId);
                        }
                    } catch (NumberFormatException e) {
                        catchCounter++;
                    }
                }
                if (catchCounter != 0) {
                    embedBuilder.setDescription("Please enter numeric id/ids of to-do row.").setColor(Color.RED);
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }

                if (!(wrongList.isEmpty())) {
                    embedBuilder.setDescription("Invalid row id/ids: " + wrongList).setColor(Color.RED);
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }

                for (Integer rowId : removeList) {
                    todoService.delete(todoList.get(rowId - 1));
                }
                if (!(removeList.isEmpty())) {
                    embedBuilder.setDescription(removeList + " row/rows of to-do list deleted!").setColor(Color.GREEN);
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }

            }
        }

        if (messageSent.startsWith("!todo update")) {
            counter(userId, userWithTag);
            String update = messageSent.replaceAll("\\s+", " ").substring(12);
            if (update.isEmpty()) {
                String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                embedBuilder.setDescription(user + " Please write something after !todo update command." +
                        "\ne.g. !todo update 1 text goes here").setColor(Color.RED);
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            } else {
                try {
                    int rowId = Integer.parseInt(update.split("\\s")[1]);
                    List<Todo> todoList = todoService.todoList(userId);
                    if (rowId > 0 && rowId <= todoList.size()) {
                        if (update.length() > 3) {
                            String sentence = update.substring(3);
                            todoList.get(rowId - 1).setTodoRow(sentence);
                            todoService.save(todoList.get(rowId - 1));
                            embedBuilder.setDescription(rowId + ". row of to-do list updated!").setColor(Color.GREEN);
                        } else {
                            embedBuilder.setDescription("Please enter a valid sentence after row id." +
                                    "\ne.g. !todo update 1 text goes here").setColor(Color.RED);
                        }
                    } else {
                        embedBuilder.setDescription("Please enter a valid row id of your to-do list without '.'")
                                .setColor(Color.RED);
                    }
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                } catch (NumberFormatException e) {
                    embedBuilder.setDescription("Please enter numeric id of to-do row.").setColor(Color.RED);
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }
            }
        }

        if (messageSent.startsWith("!todo complete")) {
            counter(userId, userWithTag);
            String complete = messageSent.replaceAll("\\s", "").substring(13);
            if (complete.isEmpty()) {
                String user = Objects.requireNonNull(event.getMessage().getMember()).getAsMention();
                embedBuilder.setDescription(user + " Please write something after !todo complete command." +
                        "\ne.g. !todo complete 1").setColor(Color.RED);
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            } else {
                try {
                    int rowId = Integer.parseInt(complete);
                    List<Todo> todoList = todoService.todoList(userId);
                    if (rowId > 0 && rowId <= todoList.size()) {
                        todoList.get(rowId - 1).setCompleted(true);
                        todoService.save(todoList.get(rowId - 1));
                        embedBuilder.setDescription(rowId + ". row of to-do list completed!").setColor(Color.GREEN);
                    } else {
                        embedBuilder.setDescription("Please enter a valid row id of your to-do list without '.'")
                                .setColor(Color.RED);
                    }
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                } catch (NumberFormatException e) {
                    embedBuilder.setDescription("Please enter numeric id of to-do row.").setColor(Color.RED);
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                }
            }
        }
    }

    private void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag);
        }
        user.setTodoCount(user.getTodoCount() + 1);
        userService.save(user);
    }
}
