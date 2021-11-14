package com.discord.bot.Event;

import com.discord.bot.Entity.User;
import com.discord.bot.Service.PostService;
import com.discord.bot.Service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        if (!event.isWebhookMessage()) {
            String messageSent = event.getMessage().getContentRaw();
            String userId = Objects.requireNonNull(event.getMember()).getUser().getId();
            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (messageSent.equalsIgnoreCase("!Guilds") && userId.equals(ADMIN)) {

                embedBuilder.setDescription("Guild Count: " + event.getJDA().getGuilds().size() +
                        "\nGuilds: " + event.getJDA().getGuilds());
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }

            if (messageSent.equalsIgnoreCase("!status") && userId.equals(ADMIN)) {

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

            if (messageSent.startsWith("!stats") && userId.equals(ADMIN)) {
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

            if (messageSent.equalsIgnoreCase("!users") && userId.equals(ADMIN)) {
                List<User> users = userService.getUsers();
                if (users.isEmpty()) {
                    embedBuilder.setDescription("No users found.");
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                } else {
                    try {
                        File usersFile = new File("users.txt");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile, true));
                        writer.write("USER COUNT: " + users.size() + "\nT: Text   H: Hentai   P: Porn   R: Reddit   T: Todo" +
                                "\n        ID             USER     T H P R T");
                        for (User user : users) {
                            writer.append("\n" + user.getUserId() + " " + user.getUserWithTag() + " "
                                    + user.getTextCount() + " " + user.getHCount() + " "
                                    + user.getPCount() + " " + user.getRedditCount() + " "
                                    + user.getTodoCount());
                        }
                        writer.close();
                        event.getChannel().sendFile(usersFile).queue();
                        Thread.sleep(10);
                        usersFile.delete();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //if bot outputs any log this command will work.
            if (messageSent.equalsIgnoreCase("!logs") && userId.equals(ADMIN)) {
                event.getChannel().sendFile(new File("logs.log")).queue();
            }
        }
    }
}
