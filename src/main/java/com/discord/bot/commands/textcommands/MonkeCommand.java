package com.discord.bot.commands.textcommands;

import com.discord.bot.commands.ISlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MonkeCommand implements ISlashCommand {
    TextCommandUtils utils;

    List<String> monkeList = Arrays.asList(
            "https://www.youtube.com/watch?v=5WTgEu5YJmw",
            "https://www.youtube.com/watch?v=Z00nVaTXl_M",
            "https://www.youtube.com/watch?v=0pANbBQkhf4",
            "https://www.youtube.com/watch?v=zsa3I5lpUmA",
            "https://www.youtube.com/watch?v=6G7HYqjBxgg",
            "https://www.youtube.com/watch?v=c1s3Iekns9k",
            "https://www.youtube.com/watch?v=cTiC_ZFVxGU",
            "https://www.youtube.com/watch?v=VLwpPYS3MFA",
            "https://www.youtube.com/watch?v=98KnORXP31k",
            "https://www.youtube.com/watch?v=3D45gUJovig",
            "https://www.youtube.com/watch?v=fBTOjJYbGEE",
            "https://www.youtube.com/watch?v=dXOIfxHQILA",
            "https://www.youtube.com/watch?v=qdIBGoO6pMk",
            "https://www.youtube.com/watch?v=0GCmXOT428s",
            "https://www.youtube.com/watch?v=1J5Mwajm60A",
            "https://www.youtube.com/watch?v=uBxRLw_YuSw",
            "https://www.youtube.com/watch?v=JBEOypYwpw0");

    public MonkeCommand(TextCommandUtils utils) {
        this.utils = utils;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply(monkeList.get(new Random().nextInt(monkeList.size()))).queue();

        net.dv8tion.jda.api.entities.User user = event.getUser();
        utils.counter(user.getId(), user.getAsTag());
    }
}