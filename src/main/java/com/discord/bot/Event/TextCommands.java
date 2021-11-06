package com.discord.bot.Event;

import com.discord.bot.Entity.User;
import com.discord.bot.Service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TextCommands extends ListenerAdapter {

    UserService userService;

    public TextCommands(UserService userService) {
        this.userService = userService;
    }

    Random random = new Random();
    List<String> wrongSyntaxList = Arrays.asList("![subreddit]", "![unexpected]", "![dankmemes]", "![memes]",
            "![greentext]", "![hentai]", "![porn]");

    List<String> monkeList = Arrays.asList(
            "https://www.youtube.com/watch?v=5WTgEu5YJmw",
            "https://www.youtube.com/watch?v=2EKKMof_Ywg",
            "https://www.youtube.com/watch?v=0pANbBQkhf4",
            "https://www.youtube.com/watch?v=zsa3I5lpUmA",
            "https://www.youtube.com/watch?v=6G7HYqjBxgg",
            "https://www.youtube.com/watch?v=c1s3Iekns9k",
            "https://www.youtube.com/watch?v=cTiC_ZFVxGU");

    String subreddits = """
                            - Unexpected
                            - dankmemes
                            - memes
                            - greentext
                            - hentai
                            - porn
                            """;

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String messageSent = event.getMessage().getContentRaw();
        boolean isBot = event.getMember().getUser().isBot();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String userId = event.getMember().getUser().getId();
        String userWithTag = event.getMember().getUser().getAsTag();

        if (messageSent.equalsIgnoreCase("hi") && !isBot) {
            counter(userId, userWithTag);
            event.getChannel().sendMessage("hi! " + event.getMember().getUser().getName()).queue();
        }

        if (messageSent.equalsIgnoreCase("!help") && !isBot) {
            counter(userId, userWithTag);
            embedBuilder.setTitle("Commands").setDescription("""
                    - ![subreddit]
                    - !how gay [user]
                    - !errrkek [user] (this command calculate how man are you)
                    - Messages include 69, 420 or both
                    - Messages equals to hi
                    - Messages include monke or monkey
                    - !github
                    - !todo add [to-do sentence]
                    - !todo list
                    - !todo remove [todo row id/ids seperated with space]
                    - !todo complete [row id]
                    - !todo update [row id]
                    """)
                    .addField("Subreddits", subreddits, false);

            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if (wrongSyntaxList.contains(messageSent.toLowerCase()) && !isBot) {
            embedBuilder.setTitle("Wrong syntax").setDescription("Use ![subreddit] without brackets. \n e.g. !memes")
                    .addField("Subreddits", subreddits, false);
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if (messageSent.contains("monkey") && !isBot) {
            counter(userId, userWithTag);
            event.getChannel().sendMessage("```" + event.getMember().getEffectiveName()
                    + ": monkey" + "\nmonke*```").queue();
        }

        if (messageSent.contains("monke") && !(messageSent.contains("monkey")) && !isBot) {
            counter(userId, userWithTag);
            event.getChannel().sendMessage("```" + event.getMember().getEffectiveName()
                    + ": monke```" + monkeList.get(random.nextInt(monkeList.size()))).queue();
        }

        if (messageSent.contains("69") && !(messageSent.contains("420")) && !isBot) {
            counter(userId, userWithTag);
            event.getChannel().sendMessage("```" + event.getMember().getEffectiveName()
                    + ": 69" + "\nNice!```").queue();
        }
        if (messageSent.contains("420") && !(messageSent.contains("69")) && !isBot) {
            counter(userId, userWithTag);
            event.getChannel().sendMessage("```" + event.getMember().getEffectiveName()
                    + ": 420" + "\nNice!```").queue();
        }
        if (messageSent.contains("69") && messageSent.contains("420") && !isBot) {
            counter(userId, userWithTag);
            event.getChannel().sendMessage("```" + event.getMember().getEffectiveName()
                    + ": 69 420```" + "https://www.youtube.com/watch?v=3WAOxKOmR90").queue();
        }
        if (messageSent.equalsIgnoreCase("!github")) {
            counter(userId, userWithTag);
            embedBuilder.setTitle("My github", "https://github.com/Glaxier0")
                    .setDescription("[Bot codes](https://github.com/Glaxier0/discord-bot-reddit-java)" +
                            "\n[Free vimeo version](https://github.com/Glaxier0/discord-bot-reddit-java-free-version)")
            .setFooter("Please read README before using codes");
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if (messageSent.startsWith("!how gay") && !isBot) {
            counter(userId, userWithTag);
            String user = messageSent.replaceAll("\\s+", " ").substring(8);

            if (user.isEmpty()) {
                embedBuilder.setDescription("Please write something after !how gay command.\ne.g. !how gay @Glaxier");
            } else {
                embedBuilder.setDescription(user + " is " + random.nextInt(100) + "% gay :gay_pride_flag: " +
                        ":gay_pride_flag: :gay_pride_flag:");
            }

            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if (messageSent.startsWith("!errrkek") && !isBot) {
            counter(userId, userWithTag);
            String user = messageSent.replaceAll("\\s+", " ").substring(8);

            if (user.isEmpty()) {
                embedBuilder.setDescription("Please write something after !errrkek command.\ne.g. !errrkek @Glaxier");
            } else {
                embedBuilder.setDescription(user + " " + random.nextInt(100)
                        + "% errrkek :muscle: :muscle: :muscle:");
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }
    }
    public void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag);
        }
        user.setTextCount(user.getTextCount() + 1);
        userService.save(user);
    }
}
