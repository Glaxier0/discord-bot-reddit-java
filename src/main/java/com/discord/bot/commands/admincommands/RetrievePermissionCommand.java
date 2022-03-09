package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.service.GuildService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.List;

public class RetrievePermissionCommand implements ISlashCommand {
    GuildService guildService;
    String ADMIN = "315403352496275456";

    public RetrievePermissionCommand(GuildService guildService) {
        this.guildService = guildService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            String guildId = event.getOption("guildid").getAsString();
            com.discord.bot.entity.Guild dbGuild = guildService.getByGuildId(guildId);
            if (dbGuild != null) {
                guildService.delete(dbGuild);
                Guild guild = event.getJDA().getGuildById(guildId);
                deleteCommandsFromGuild(guild);
                event.replyEmbeds(new EmbedBuilder().setDescription("Guild removed from premium list.").build()).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder().setDescription("Guild not found in premium list.").build()).queue();
            }
        }
    }

    private void deleteCommandsFromGuild(Guild guild) throws IllegalStateException {
        RestAction<List<Command>> guildCommands = guild.retrieveCommands();
        List<Command> commandList = guildCommands.complete();
        for (Command command : commandList) {
            command.delete();
        }
        guild.updateCommands().queue();
    }
}
