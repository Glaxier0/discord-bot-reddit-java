package com.discord.bot.Event;

import com.discord.bot.Service.PostService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.List;
import java.util.Objects;

public class AdminCommands extends ListenerAdapter {

    //Edit this admin name with your discord name with tag
    protected final String ADMIN = "temp#1234";
    PostService service;

    public AdminCommands(PostService service) {
        this.service = service;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String messageSent = event.getMessage().getContentRaw();
        String userWithTag = Objects.requireNonNull(event.getMember()).getUser().getAsTag();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        boolean isBot = Objects.requireNonNull(event.getMember()).getUser().isBot();

        if (messageSent.equalsIgnoreCase("!Guilds") &&
                userWithTag.equals(ADMIN) && !isBot) {

            embedBuilder.setDescription("Guild Count: " + event.getJDA().getGuilds().size() +
                    "\nGuilds: " + event.getJDA().getGuilds());
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if (messageSent.equalsIgnoreCase("!status") && userWithTag.equals(ADMIN) && !isBot) {

            int videoCount = service.getVideoNullVimeo().size();
            List<String> subredditPostCount = service.getSubredditCount();

            for (int i = 0; i < subredditPostCount.size(); i++) {
                subredditPostCount.set(i, subredditPostCount.get(i).replace(",", ": "));
            }

            embedBuilder.setTitle("Status")
                    .setDescription("Video count to be uploaded: " + videoCount)
                    .addField("Subreddit post counts", subredditPostCount.toString()
                            .replace("[", "")
                            .replace("]", "")
                            .replace(",", "\n"), true);
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }
    }
}
