package com.discord.bot.commands.textcommands;

import com.discord.bot.commands.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HelpCommand implements ISlashCommand {
    final TextCommandUtils utils;

    final String subreddits = """
            - Unexpected
            - dankmemes
            - memes
            - greentext
            - blursedimages
            - perfectlycutscreams
            - interestingasfuck
            - facepalm
            """;

    public HelpCommand(TextCommandUtils utils) {
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Commands").setDescription("""
                        - /[subreddit_name]
                        - /howgay
                        - /errrkek
                        - /monke
                        - /github
                        - /top.gg
                        - /todoadd
                        - /todolist
                        - /todoremove
                        - /todocomplete
                        - /todoupdate
                        - ||For NSFW||
                        - ||Use /nhelp||
                        - ||in age restricted||
                        - ||channel||
                        """)
                .addField("Subreddits", subreddits, false);

        event.replyEmbeds(embedBuilder.build()).queue();

        User user = event.getUser();
        utils.counter(user.getId(), user.getName());
    }
}
