package com.discord.bot.Event;

import com.discord.bot.Entity.User;
import com.discord.bot.Service.PostService;
import com.discord.bot.Service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Objects;

public class AdminCommands extends ListenerAdapter {

    protected final String ADMIN = "your discord id";
    PostService postService;
    UserService userService;

    public AdminCommands(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
        String userId = event.getMember().getUser().getId();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        boolean isBot = event.getMember().getUser().isBot();

        if (messageSent.equalsIgnoreCase("!Guilds") &&
                userId.equals(ADMIN) && !isBot) {

            embedBuilder.setDescription("Guild Count: " + event.getJDA().getGuilds().size() +
                    "\nGuilds: " + event.getJDA().getGuilds());
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        if (messageSent.equalsIgnoreCase("!status") && userId.equals(ADMIN) && !isBot) {

            int videoCount = postService.getVideoNullFirebase().size();
            List<String> subredditPostCount = postService.getSubredditCount();

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

        if (messageSent.startsWith("!stats") && userId.equals(ADMIN) && !isBot) {
            List<net.dv8tion.jda.api.entities.User> userList = event.getMessage().getMentionedUsers();

            for (net.dv8tion.jda.api.entities.User userStats : userList) {
                User user = userService.getUser(userStats.getId());
                if (user == null) {
                    embedBuilder.setDescription("No data found on user " + userStats.getAsMention());
                } else {
                    embedBuilder.setDescription("User: " + userStats.getAsMention() +
                            "\nText commands: " + user.getTextCount() +
                            "\nHentai commands: " + user.getHCount() +
                            "\nPorn commands: " + user.getPCount() +
                            "\nReddit commands: " + user.getRedditCount() +
                            "\nTodo commands: " + user.getTodoCount());
                }
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }

        if (messageSent.equalsIgnoreCase("!users") && userId.equals(ADMIN) && !isBot) {
            List<User> users = userService.getUsers();
            if (users.isEmpty()) {
                embedBuilder.setDescription("No users found.");
            } else {
                embedBuilder.setDescription("**USER COUNT: " + users.size()
                        + "**\n__**``T: Text   H: Hentai   P: Porn   R: Reddit``**__\n__**``    USER    " + "        USER ID     " + "  T"
                        + " H" + " P" + " R``**__\n");
                for (User user : users) {
                    embedBuilder.appendDescription(user.getUserWithTag() + " " + user.getUserId() + " "
                            + user.getTextCount() + " " + user.getHCount() + " "
                            + user.getPCount() + " " + user.getRedditCount() + "\n");
                }
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }
    }
}
