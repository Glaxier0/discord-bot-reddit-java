package com.discord.bot.commands.textcommands;

import com.discord.bot.commands.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Random;

public class HowManCommand implements ISlashCommand {
    TextCommandUtils utils;

    Random random = new Random();

    public HowManCommand(TextCommandUtils utils) {
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User userToCalculate = event.getOption("user").getAsUser();

        embedBuilder.setDescription(userToCalculate.getAsMention() + "is " + random.nextInt(100)
                + "% errrkek :muscle: :muscle: :muscle:");
        event.replyEmbeds(embedBuilder.build()).queue();

        net.dv8tion.jda.api.entities.User user = event.getUser();
        utils.counter(user.getId(), user.getAsTag());
    }
}
