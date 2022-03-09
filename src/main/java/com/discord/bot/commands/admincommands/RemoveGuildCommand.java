package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.service.GuildService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RemoveGuildCommand implements ISlashCommand {
    GuildService guildService;
    String ADMIN = "315403352496275456";

    public RemoveGuildCommand(GuildService guildService) {
        this.guildService = guildService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            String guildId = event.getOption("guildid").getAsString();
            com.discord.bot.entity.Guild guild = guildService.getByGuildId(guildId);
            if (guild != null) {
                guildService.delete(guild);
                embedBuilder.setDescription("Guild deleted from approved guilds.");
            } else {
                embedBuilder.setDescription("Guild not found");
            }
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }
}
