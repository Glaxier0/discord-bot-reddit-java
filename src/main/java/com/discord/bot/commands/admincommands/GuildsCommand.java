package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@AllArgsConstructor
public class GuildsCommand implements ISlashCommand {
    private final String adminUserId;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(adminUserId)) {
            try {
                File guildsFile = new File("guilds.txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(guildsFile, true));
                writer.write("GUILD COUNT: " + event.getJDA().getGuilds().size() + "\n        ID         NAME");

                for (Guild guild : event.getJDA().getGuilds()) {
                    writer.append("\n").append(guild.getId())
                            .append(" ").append(guild.getName());
                }

                writer.close();
                event.replyFiles(FileUpload.fromData(guildsFile)).queue();
                Thread.sleep(100);
                //noinspection ResultOfMethodCallIgnored
                guildsFile.delete();
            } catch (IOException | InterruptedException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
    }
}
