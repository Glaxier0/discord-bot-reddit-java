package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.service.GuildService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class AddGuildCommand implements ISlashCommand {
    GuildService guildService;
    String ADMIN = "315403352496275456";

    public AddGuildCommand(GuildService guildService) {
        this.guildService = guildService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            String guildName = event.getOption("guildname").getAsString();
            String guildId = event.getOption("guildid").getAsString();

            if (guildService.getByGuildId(guildId) == null) {
                guildService.save(new com.discord.bot.entity.Guild(guildId, guildName));
                embedBuilder.setDescription("Guild added to approved guilds.");
            } else {
                embedBuilder.setDescription("Guild already approved");
            }
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }
}
