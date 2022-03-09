package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.entity.Guild;
import com.discord.bot.service.GuildService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class GetGuildsCommand implements ISlashCommand {
    GuildService guildService;
    String ADMIN = "315403352496275456";

    public GetGuildsCommand(GuildService guildService) {
        this.guildService = guildService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            List<Guild> guilds = guildService.findAll();
            embedBuilder.setTitle("Approved Guilds");
            for (Guild guild : guilds) {
                embedBuilder.appendDescription(guild.getGuildId() + " " + guild.getGuildName() + "\n");
            }
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }
}
