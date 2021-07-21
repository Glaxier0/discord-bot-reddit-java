package com.discord.bot.Event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TextCommands extends ListenerAdapter {

    List<String> list = Arrays.asList("![subreddit]", "![unexpected]", "![dankmemes]", "![memes]", "![greentext]");

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String messageSent = event.getMessage().getContentRaw();
        boolean isBot = Objects.requireNonNull(event.getMember()).getUser().isBot();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (messageSent.equalsIgnoreCase("hi") && !isBot) {
            event.getChannel().sendMessage("hi! " + event.getMember().getUser().getName()).queue();
        }

        if (messageSent.equalsIgnoreCase("!help") && !isBot) {
            embedBuilder.setTitle("Commands").setDescription("""
                    - ![subreddit]
                    - Messages include 69, 420 or both
                    - Messages equals to hi""")
                    .addField("Subreddits", "- Unexpected\n- dankmemes\n- memes\n- greentext", false);

            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if (list.contains(messageSent.toLowerCase()) && !isBot) {
            embedBuilder.setTitle("Wrong syntax").setDescription("Use ![subreddit] without brakets. \n e.g !memes")
                    .addField("Subreddits", "- Unexpected\n- dankmemes\n- memes\n- greentext", false);
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if (messageSent.contains("69") && !(messageSent.contains("420")) && !isBot) {
            event.getChannel().sendMessage("```" + event.getMember().getEffectiveName()
                    + ": 69" + "\nNice!```").queue();
        }
        if (messageSent.contains("420") && !(messageSent.contains("69")) && !isBot) {
            event.getChannel().sendMessage("```" + event.getMember().getEffectiveName()
                    + ": 420" + "\nNice!```").queue();
        }
        if (messageSent.contains("69") && messageSent.contains("420") && !isBot) {
            event.getChannel().sendMessage("```" + event.getMember().getEffectiveName()
                    + ": 69 420```" + "https://www.youtube.com/watch?v=3WAOxKOmR90").queue();
        }
    }
}
