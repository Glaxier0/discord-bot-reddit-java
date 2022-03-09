package com.discord.bot.commands.textcommands;

import com.discord.bot.commands.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class GithubCommand implements ISlashCommand {
    TextCommandUtils utils;

    public GithubCommand(TextCommandUtils utils) {
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("My github", "https://github.com/Glaxier0")
                .setDescription("[Bot codes](https://github.com/Glaxier0/discord-bot-reddit-java)")
                .setFooter("Please read README before using codes");
        event.replyEmbeds(embedBuilder.build()).queue();

        net.dv8tion.jda.api.entities.User user = event.getUser();
        utils.counter(user.getId(), user.getAsTag());
    }
}
