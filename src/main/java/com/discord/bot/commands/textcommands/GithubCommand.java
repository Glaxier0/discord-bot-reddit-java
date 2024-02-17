package com.discord.bot.commands.textcommands;

import com.discord.bot.commands.ISlashCommand;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@AllArgsConstructor
public class GithubCommand implements ISlashCommand {
    TextCommandUtils utils;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("My github", "https://github.com/Glaxier0")
                .setDescription("[Bot codes](https://github.com/Glaxier0/discord-bot-reddit-java)")
                .setFooter("Please read README before using codes");
        event.replyEmbeds(embedBuilder.build()).queue();

        User user = event.getUser();
        utils.counter(user.getId(), user.getAsTag());
    }
}
