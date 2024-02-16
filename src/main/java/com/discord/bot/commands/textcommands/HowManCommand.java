package com.discord.bot.commands.textcommands;

import com.discord.bot.commands.ISlashCommand;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;
import java.util.Random;

@AllArgsConstructor
public class HowManCommand implements ISlashCommand {
    TextCommandUtils utils;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User userToCalculate = Objects.requireNonNull(event.getOption("user")).getAsUser();

        embedBuilder.setDescription(userToCalculate.getAsMention() + " is " + new Random().nextInt(100)
                + "% errrkek :muscle: :muscle: :muscle:");
        event.replyEmbeds(embedBuilder.build()).queue();

        User user = event.getUser();
        utils.counter(user.getId(), user.getAsTag());
    }
}
