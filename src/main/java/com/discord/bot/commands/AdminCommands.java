package com.discord.bot.commands;

import com.discord.bot.entity.User;
import com.discord.bot.service.GuildService;
import com.discord.bot.service.PostService;
import com.discord.bot.service.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AdminCommands extends ListenerAdapter {

    PostService postService;
    UserService userService;
    GuildService guildService;
    String ADMIN;

    public AdminCommands(PostService postService, UserService userService, GuildService guildService, String ADMIN) {
        this.postService = postService;
        this.userService = userService;
        this.guildService = guildService;
        this.ADMIN = ADMIN;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            switch (event.getName()) {
                case "guilds" -> guilds(event);
                case "status" -> status(event);
                case "stats" -> stats(event, event.getOption("user").getAsUser());
                case "users" -> users(event);
                case "logs" -> logs(event);
                case "getguild" -> getGuild(event);
                case "addguild" -> addGuild(event);
                case "removeguild" -> removeGuild(event);
                case "getguilds" -> getGuilds(event);
            }
        }
    }

    private void guilds(SlashCommandInteractionEvent event) {
        try {
            File guildsFile = new File("guilds.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(guildsFile, true));
            writer.write("GUILD COUNT: " + event.getJDA().getGuilds().size() + "\n        ID         NAME");

            for (Guild guild : event.getJDA().getGuilds()) {
                writer.append("\n" + guild.getId() + " " + guild.getName());
            }

            writer.close();
            event.replyFile(guildsFile).queue();
            Thread.sleep(100);
            guildsFile.delete();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void status(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
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
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void stats(SlashCommandInteractionEvent event, net.dv8tion.jda.api.entities.User userStats) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        User user = userService.getUser(userStats.getId());
        if (user == null) {
            embedBuilder.setDescription("No data found on user " + userStats.getAsMention());
        } else {
            embedBuilder.setDescription("User: " + userStats.getAsMention() +
                    "\nText commands: " + user.getTextCount() +
                    "\nHentai commands: " + user.getHCount() +
                    "\nPorn commands: " + user.getPCount() +
                    "\nReddit commands: " + user.getRedditCount() +
                    "\nTodo commands: " + user.getTodoCount() +
                    "\nMusic commands: " + user.getMusicCount());
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void users(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        List<User> users = userService.getUsers();
        if (users.isEmpty()) {
            embedBuilder.setDescription("No users found.");
            event.replyEmbeds(embedBuilder.build()).queue();
        } else {
            try {
                File usersFile = new File("users.txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile, true));
                writer.write("USER COUNT: " + users.size() + "\nT: Text   H: Hentai   P: Porn   " +
                        "R: Reddit   T: Todo     M: Music" +
                        "\n        ID             USER      T H P R T M");
                for (User user : users) {
                    writer.append("\n" + user.getUserId() + " " + user.getUserWithTag() + " "
                            + user.getTextCount() + " " + user.getHCount() + " "
                            + user.getPCount() + " " + user.getRedditCount() + " "
                            + user.getTodoCount() + " " + user.getMusicCount());
                }
                writer.close();
                event.replyFile(usersFile).queue();
                Thread.sleep(100);
                usersFile.delete();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void logs(SlashCommandInteractionEvent event) {
        File logs = new File("logs.log");
        event.replyFile(logs).queue();
    }

    private void getGuild(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(event.getGuild().getId());
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void addGuild(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String guildName = event.getOption("guildname").getAsString();
        String guildId = event.getOption("guildid").getAsString();

        if (guildService.getByGuildId(guildId) == null) {
            guildService.save(new com.discord.bot.entity.Guild(guildId, guildName));
            embedBuilder.setDescription("Guild added to approved guilds.");
        } else {
            embedBuilder.setDescription("Guild already approved");
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void removeGuild(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String guildId = event.getOption("guildid").getAsString();
        com.discord.bot.entity.Guild guild = guildService.getByGuildId(guildId);
        if (guild != null) {
            guildService.delete(guild);
            embedBuilder.setDescription("Guild deleted from approved guilds.");
        } else {
            embedBuilder.setDescription("Guild not found");
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void getGuilds(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        List<com.discord.bot.entity.Guild> guilds = guildService.findAll();
        embedBuilder.setTitle("Approved Guilds");
        for (com.discord.bot.entity.Guild guild : guilds) {
            embedBuilder.appendDescription(guild.getGuildId() + " " + guild.getGuildName() + "\n");
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
