package com.discord.bot;

import com.discord.bot.commands.*;
import com.discord.bot.service.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.security.auth.login.LoginException;
import java.io.IOException;

@Configuration
@EnableScheduling
public class Bot {

    PostService postService;
    TodoService todoService;
    UserService userService;
    GuildService guildService;
    SearchReddit redditSearchService;
    DownloadVideos downloadVideosService;
    RemoveOldPosts removeOldPostsService;
    RestService restService;

    @Value("${reddit_username}")
    private String REDDIT_USERNAME;
    @Value("${reddit_password}")
    private String REDDIT_PASSWORD;
    @Value("${reddit_client_id}")
    private String REDDIT_CLIENT_ID;
    @Value("${reddit_client_secret}")
    private String REDDIT_CLIENT_SECRET;
    @Value("${discord_bot_token}")
    private String DISCORD_TOKEN;
    @Value("${firebase_storage_bucket_name}")
    private String BUCKET_NAME;
    @Value("${test_server_id}")
    private String TEST_SERVER;
    @Value("${discord_admin_id}")
    private String ADMIN;

    public Bot(PostService postService, TodoService todoService, UserService userService, GuildService guildService,
               SearchReddit redditSearchService, DownloadVideos downloadVideosService,
               RemoveOldPosts removeOldPostsService, RestService restService) {
        this.postService = postService;
        this.todoService = todoService;
        this.userService = userService;
        this.guildService = guildService;
        this.redditSearchService = redditSearchService;
        this.downloadVideosService = downloadVideosService;
        this.removeOldPostsService = removeOldPostsService;
        this.restService = restService;
    }

    @Bean
    public void startDiscordBot() {
        try {
            JDA jda = JDABuilder.createDefault(DISCORD_TOKEN)
                    .addEventListeners(new RedditCommands(postService, userService),
                            new TextCommands(userService),
                            new AdminCommands(postService, userService, guildService, ADMIN),
                            new NsfwCommands(postService, userService),
                            new ToDoCommands(todoService, userService),
                            new VoiceCommands(guildService, restService, userService),
                            new PermissionCommands(guildService))
                    .setActivity(Activity.playing("Please check bot description and new prefix /help")).build();

            addCommands(jda);
            System.out.println("Starting bot is done!");
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    private void addCommands(JDA jda) {
        while (jda.getGuildById(TEST_SERVER) == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Guild testServer = jda.getGuildById(TEST_SERVER);

        CommandListUpdateAction testServerCommands = testServer.updateCommands();
        CommandListUpdateAction globalCommands = jda.updateCommands();

        testServerCommands.addCommands(
                //admin commands
                Commands.slash("givepermission", "Give permission to guild")
                        .addOptions(new OptionData(OptionType.STRING, "guildid", "Guild id.")
                                        .setRequired(true),
                                new OptionData(OptionType.STRING, "guildname", "Guild name.")
                                        .setRequired(false)),
                Commands.slash("retrievepermission", "Retrieve permission from guild")
                        .addOptions(new OptionData(OptionType.STRING, "guildid", "Guild id.")
                                .setRequired(true)),
                Commands.slash("guilds", "Get guild list that bot is in."),
                Commands.slash("status", "Get reddit post statuses."),
                Commands.slash("stats", "Get user stats.")
                        .addOptions(new OptionData(OptionType.MENTIONABLE, "user", "User with mention.")
                                .setRequired(true)),
                Commands.slash("users", "Get bot users."),
                Commands.slash("logs", "Get logs."),
                Commands.slash("getguild", "Get guild id."),
                Commands.slash("addguild", "Add guild to the premium list.")
                        .addOptions(new OptionData(OptionType.STRING, "guildname", "Guild name.")
                                .setRequired(true))
                        .addOptions(new OptionData(OptionType.STRING, "guildid", "Guild ID.")
                                .setRequired(true)
                        ),
                Commands.slash("removeguild", "Remove guild from premium list.")
                        .addOptions(new OptionData(OptionType.STRING, "guildid", "Guild ID.")
                                .setRequired(true)),
                Commands.slash("getguilds", "Get premium guilds."),
                //Music Commands
                Commands.slash("play", "Play a song on your voice channel.")
                        .addOptions(new OptionData(OptionType.STRING, "query", "Song url or name.")
                                .setRequired(true)),
                Commands.slash("skip", "Skip the current song."),
                Commands.slash("pause", "Pause the current song."),
                Commands.slash("resume", "Resume paused song."),
                Commands.slash("leave", "Make bot leave voice channel."),
                Commands.slash("queue", "List song queue."),
                Commands.slash("swap", "Swap order of two songs in queue")
                        .addOptions(new OptionData(OptionType.INTEGER, "songnum1",
                                        "Song number in the queue to be changed.").setRequired(true),
                                new OptionData(OptionType.INTEGER, "songnum2",
                                        "Song number in queue to be changed.").setRequired(true)),
                Commands.slash("shuffle", "Shuffle the queue."),
                Commands.slash("mhelp", "Help page for music commands.")
        ).queue();

        globalCommands.addCommands(
                //nsfw commands
                Commands.slash("hentai", "Get random hentai image/gif/video."),
                Commands.slash("porn", "Get random porn image/gif/video."),
                //reddit commands
                Commands.slash("unexpected", "Get top r/unexpected posts."),
                Commands.slash("dankmemes", "Get top r/dankmemes posts."),
                Commands.slash("memes", "Get top r/memes posts."),
                Commands.slash("greentext", "Get top r/greentext posts."),
                Commands.slash("blursedimages", "Get top r/blursedimages posts."),
                Commands.slash("perfectlycutscreams", "Get top r/perfectlycutscreams posts."),
                Commands.slash("interestingasfuck", "Get top r/interestingasfuck posts."),
                Commands.slash("facepalm", "Get top r/facepalm posts."),
                //test commands
                Commands.slash("help", "Info page about bot commands"),
                Commands.slash("monke", "Get my favorite random monke video."),
                Commands.slash("github", "My github page and source code of bot."),
                Commands.slash("howgay", "Calculate how gay is someone.")
                        .addOptions(new OptionData(OptionType.USER, "user", "User to calculate how gay.")
                                .setRequired(true)),
                Commands.slash("errrkek", "Calculate how man is someone.")
                        .addOptions(new OptionData(OptionType.USER, "user", "User to calculate how man.")
                                .setRequired(true)),
                Commands.slash("topgg", "Top.gg page of Glaxier bot."),
                //to-do commands
                Commands.slash("todoadd", "Add a task to your to-do list.")
                        .addOptions(new OptionData(OptionType.STRING, "task",
                                "A to-do task.").setRequired(true)),
                Commands.slash("todolist", "Shows your to-do list."),
                Commands.slash("todoremove", "Remove a task from your to-do list.")
                        .addOptions(new OptionData(OptionType.INTEGER, "taskid",
                                "To-do task id to remove.").setRequired(true)),
                Commands.slash("todoupdate", "Update a task in your to-do list.")
                        .addOptions(new OptionData(OptionType.INTEGER, "taskid",
                                        "To-do task id to remove.").setRequired(true),
                                new OptionData(OptionType.STRING, "task",
                                        "Updated to-do task.").setRequired(true)),
                Commands.slash("todocomplete", "Complete a task in your to-do list.")
                        .addOptions(new OptionData(OptionType.INTEGER, "taskid",
                                "To-do task id to remove.").setRequired(true)),
                Commands.slash("todoclear", "Clears your to-do list.")
        ).queue();
    }

    @Scheduled(fixedDelay = 7200000)
    private void twoHourDelay() {
        redditSearchService.searchReddit(postService, REDDIT_USERNAME, REDDIT_PASSWORD, REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET);
        downloadVideosService.downloadVideos(postService, BUCKET_NAME);
    }

    @Scheduled(fixedDelay = 86400000)
    public void dayDelay() throws IOException {
        removeOldPostsService.removeOldPosts(postService);
        removeOldPostsService.removeOldFirebaseVideos(postService, BUCKET_NAME);
    }
}




