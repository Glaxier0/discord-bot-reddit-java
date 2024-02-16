package com.discord.bot;

import com.discord.bot.commands.CommandManager;
import com.discord.bot.commands.CustomCommands;
import com.discord.bot.commands.JdaCommands;
import com.discord.bot.commands.TestCommands;
import com.discord.bot.service.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class Bot {
    TodoService todoService;
    UserService userService;
    PostService postService;
    SubredditService subredditService;

    SearchReddit redditSearchService;
    RemoveOldPosts removeOldPostsService;
    FirebaseServiceImpl firebaseService;
    RedditTokenService redditTokenService;
    RedgifsTokenService redgifsTokenService;

    @Value("${discord_bot_token}")
    private String DISCORD_TOKEN;

    @Value("${test_server_id}")
    private String TEST_SERVER;

    public Bot(PostService postService, SubredditService subredditService,
               TodoService todoService, UserService userService,
               SearchReddit redditSearchService, RedditTokenService redditTokenService,
               RemoveOldPosts removeOldPostsService, FirebaseServiceImpl firebaseService,
               RedgifsTokenService redgifsTokenService) {
        this.postService = postService;
        this.subredditService = subredditService;
        this.todoService = todoService;
        this.userService = userService;
        this.redditSearchService = redditSearchService;
        this.redditTokenService = redditTokenService;
        this.removeOldPostsService = removeOldPostsService;
        this.firebaseService = firebaseService;
        this.redgifsTokenService = redgifsTokenService;
    }

    @Bean
    public void startDiscordBot() {
        JDA jda = JDABuilder.createDefault(DISCORD_TOKEN)
                .addEventListeners(
                        new CommandManager(postService, subredditService, todoService, userService))
                .setActivity(Activity.playing("Type /help")).build();
        new CustomCommands().addCustomCommands(jda);
        new JdaCommands().addJdaCommands(jda);
        new TestCommands().addTestCommands(jda, TEST_SERVER);
        System.out.println("Starting bot is done!");
    }

    @Scheduled(fixedDelay = 7200000)
    private void twoHourDelay() {
        redditTokenService.getAccessToken();
        redditSearchService.searchReddit();
        firebaseService.downloadVideos();
    }

    @Scheduled(fixedDelay = 57600000)
    private void sixTeenHourDelay() {
        redgifsTokenService.getAccessToken();
    }

    @Scheduled(fixedDelay = 86400000)
    private void dayDelay() {
        removeOldPostsService.removeOldPosts();
        firebaseService.removeOldFirebaseVideos();
    }
}