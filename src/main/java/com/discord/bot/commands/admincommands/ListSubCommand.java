package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Subreddit;
import com.discord.bot.service.SubredditService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ListSubCommand implements ISlashCommand {
    SubredditService subredditService;

    String ADMIN = "315403352496275456";

    public ListSubCommand(SubredditService subredditService) {
        this.subredditService = subredditService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            List<Subreddit> subreddits = subredditService.getSubreddits();

            try {
                File subredditsFile = new File("subreddits.txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(subredditsFile, true));
                writer.write("Subreddit Count: " + subreddits.size()
                        + "\n---------------------"
                        + "\n NAME   GENRE   NSFW"
                        + "\n---------------------");

                for (Subreddit subreddit : subreddits) {
                    writer.append("\n").append(subreddit.getName())
                            .append(" ").append(subreddit.getGenre())
                            .append(" ").append(String.valueOf(subreddit.isNsfw()));
                }

                writer.close();
                event.replyFiles(FileUpload.fromData(subredditsFile)).queue();
                Thread.sleep(100);
                //noinspection ResultOfMethodCallIgnored
                subredditsFile.delete();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
