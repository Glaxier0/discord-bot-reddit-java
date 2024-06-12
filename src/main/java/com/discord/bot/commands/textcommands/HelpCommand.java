package com.discord.bot.commands.textcommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.service.SubredditService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.stream.Collectors;

public class HelpCommand implements ISlashCommand {
    final TextCommandUtils utils;
    final SubredditService subredditService;

    public HelpCommand(TextCommandUtils utils, SubredditService subredditService) {
        this.utils = utils;
        this.subredditService = subredditService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        var subreddits = subredditService.getSubredditsByGenre("reddit");
        var formattedSubreddits = subreddits.stream()
                .map(subreddit -> "- " + subreddit)
                .collect(Collectors.joining("\n", "\n", "\n"));

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
                .addField("Subreddits", formattedSubreddits, false);

        event.replyEmbeds(embedBuilder.build()).queue();

        User user = event.getUser();
        utils.counter(user.getId(), user.getName());
    }
}
