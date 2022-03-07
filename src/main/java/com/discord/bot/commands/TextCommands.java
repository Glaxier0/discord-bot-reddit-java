package com.discord.bot.commands;

import com.discord.bot.entity.User;
import com.discord.bot.service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TextCommands extends ListenerAdapter {

    UserService userService;
    Random random = new Random();

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
            "https://www.youtube.com/watch?v=uBxRLw_YuSw");

    String subreddits = """
            - Unexpected
            - dankmemes
            - memes
            - greentext
            - hentai
            - porn
            - blursedimages
            - perfectlycutscreams
            - interestingasfuck
            - facepalm
            """;

    public TextCommands(UserService userService) {
        this.userService = userService;
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "help" -> help(event);
            case "monke" -> monke(event);
            case "github" -> github(event);
            case "howgay" -> howGay(event);
            case "errrkek" -> errrkek(event);
            case "topgg" -> topGG(event);
        }
    }

    private void help(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());

        embedBuilder.setTitle("Commands").setDescription("""
                        - /[subreddit_name]
                        - /howgay [user]
                        - /errrkek [user]
                        - /monke
                        - /github
                        - /top.gg
                        - /todoadd [to-do sentence]
                        - /todolist
                        - /todoremove [todo row id/ids seperated with space]
                        - /todocomplete [row id]
                        - /todoupdate [row id]
                        """)
                .addField("Subreddits", subreddits, false);

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void monke(SlashCommandInteractionEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());

        event.reply(monkeList.get(random.nextInt(monkeList.size()))).queue();
    }

    private void github(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());

        embedBuilder.setTitle("My github", "https://github.com/Glaxier0")
                .setDescription("[Bot codes](https://github.com/Glaxier0/discord-bot-reddit-java)")
                .setFooter("Please read README before using codes");
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void howGay(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        net.dv8tion.jda.api.entities.User userToCalculate = event.getOption("user").getAsUser();
        counter(user.getId(), user.getAsTag());

        embedBuilder.setDescription(userToCalculate.getAsMention() + " is " + random.nextInt(100) + "% gay :gay_pride_flag: " +
                ":gay_pride_flag: :gay_pride_flag:");
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void errrkek(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        net.dv8tion.jda.api.entities.User userToCalculate = event.getOption("user").getAsUser();
        counter(user.getId(), user.getAsTag());

        embedBuilder.setDescription(userToCalculate.getAsMention() + "is " + random.nextInt(100)
                + "% errrkek :muscle: :muscle: :muscle:");
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void topGG(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());

        embedBuilder.setTitle("Bot's Top.gg Page", "https://top.gg/bot/855806720834928641")
                .setFooter("Please send feedback.");
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    public void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag, 0);
        }
        user.setTextCount(user.getTextCount() + 1);
        userService.save(user);
    }
}
