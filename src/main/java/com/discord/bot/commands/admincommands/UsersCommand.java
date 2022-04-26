package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class UsersCommand implements ISlashCommand {
    UserService userService;
    String ADMIN = "315403352496275456";

    public UsersCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            List<User> users = userService.getUsers();
            if (users.isEmpty()) {
                embedBuilder.setDescription("No users found.");
                event.replyEmbeds(embedBuilder.build()).queue();
            } else {
                try {
                    File usersFile = new File("users.txt");
                    BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile, true));
                    writer.write("USER COUNT: " + users.size() + "\nT: Text   H: Hentai   P: Porn   " +
                            "R: Reddit   T: Todo" +
                            "\n        ID             USER      T H P R T");
                    for (User user : users) {
                        writer.append("\n" + user.getUserId() + " " + user.getUserWithTag() + " "
                                + user.getTextCount() + " " + user.getHCount() + " "
                                + user.getPCount() + " " + user.getRedditCount() + " "
                                + user.getTodoCount());
                    }
                    writer.close();
                    event.replyFile(usersFile).queue();
                    Thread.sleep(100);
                    usersFile.delete();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
