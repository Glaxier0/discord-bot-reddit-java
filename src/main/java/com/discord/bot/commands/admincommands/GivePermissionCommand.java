package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.service.GuildService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class GivePermissionCommand implements ISlashCommand {
    GuildService guildService;
    String ADMIN = "315403352496275456";

    public GivePermissionCommand(GuildService guildService) {
        this.guildService = guildService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            String guildId = event.getOption("guildid").getAsString();
            String guildName = event.getOption("guildname").getAsString();
            if (guildService.getByGuildId(guildId) == null) {
                guildService.save(new com.discord.bot.entity.Guild(guildId, guildName));
                Guild guild = event.getJDA().getGuildById(guildId);
                addCommandsToGuild(guild);
                event.replyEmbeds(new EmbedBuilder().setDescription("Guild added to premium guild list.").build()).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder().setDescription("Guild already in premium guild list.").build()).queue();
            }
        }
    }

    private void addCommandsToGuild(Guild guild) {
        CommandListUpdateAction guildCommands = guild.updateCommands();
    }
}
