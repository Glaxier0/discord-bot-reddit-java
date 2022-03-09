package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.service.GuildService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
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

        guildCommands.addCommands(
                Commands.slash("play","Play a song on your voice channel.")
                        .addOptions(new OptionData(OptionType.STRING, "query", "Song url or name.")
                                .setRequired(true)),
                Commands.slash("skip", "Skip the current song."),
                Commands.slash("pause", "Pause the current song."),
                Commands.slash("resume", "Resume paused song."),
                Commands.slash("leave", "Make bot leave voice channel."),
                Commands.slash("queue", "List song queue."),
                Commands.slash("swap", "Swap order of two songs in queue")
                        .addOptions(new OptionData(OptionType.INTEGER, "songnum1",
                                        "Song number in the queue to be changed.").setRequired(true),
                                new OptionData(OptionType.INTEGER, "songnum2",
                                        "Song number in queue to be changed.").setRequired(true)),
                Commands.slash("shuffle", "Shuffle the queue."),
                Commands.slash("mhelp", "Help page for music commands.")
        ).queue();
    }
}
