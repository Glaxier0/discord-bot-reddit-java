package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class GetGuildCommand implements ISlashCommand {
    String ADMIN = "315403352496275456";

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setDescription(event.getGuild().getId());
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }
}
