package com.discord.bot;

import com.discord.bot.Event.*;
import com.discord.bot.Service.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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
    SearchReddit redditSearchService;
    DownloadVideos downloadVideosService;
    RemoveOldPosts removeOldPostsService;
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

    public Bot(PostService postService, TodoService todoService, UserService userService,
               SearchReddit redditSearchService, DownloadVideos downloadVideosService,
               RemoveOldPosts removeOldPostsService) {
        this.postService = postService;
        this.todoService = todoService;
        this.userService = userService;
        this.redditSearchService = redditSearchService;
        this.downloadVideosService = downloadVideosService;
        this.removeOldPostsService = removeOldPostsService;
    }

    @Bean
    public void startDiscordBot() {
        try {
            JDA jda = JDABuilder.createDefault(DISCORD_TOKEN).build();
            jda.getPresence().setActivity(Activity.playing("Type !help"));
            jda.addEventListener(new RedditCommands(postService, userService), new TextCommands(userService),
                    new AdminCommands(postService, userService),
                    new NsfwCommands(postService, userService), new ToDoCommands(todoService, userService));

            System.out.println("Starting bot is done!");
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 3600000)
    public void hourDelay() {
        redditSearchService.searchReddit(postService, REDDIT_USERNAME, REDDIT_PASSWORD, REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET);
        downloadVideosService.downloadVideos(postService, BUCKET_NAME);
    }

    @Scheduled(fixedDelay = 86400000)
    public void dayDelay() throws IOException {
        removeOldPostsService.removeOldPosts(postService);
        removeOldPostsService.removeOldFirebaseVideos(postService, BUCKET_NAME);
    }
}




