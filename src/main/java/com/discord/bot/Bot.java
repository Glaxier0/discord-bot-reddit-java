package com.discord.bot;

import com.discord.bot.commands.CommandManager;
import com.discord.bot.commands.JdaCommands;
import com.discord.bot.commands.TestCommands;
import com.discord.bot.service.*;
import jakarta.annotation.PostConstruct;
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
    final TodoService todoService;
    final UserService userService;
    final PostService postService;
    final SubredditService subredditService;

    SearchReddit redditSearchService;
    RemoveOldPosts removeOldPostsService;
    FirebaseServiceImpl firebaseService;
    RedditTokenService redditTokenService;
    RedgifsTokenService redgifsTokenService;

    @Value("${discord_bot_token}")
    private String DISCORD_TOKEN;

    @Value("${test_server_id}")
    private String TEST_SERVER;

    @Value("${admin.user.id}")
    private String adminUserId;

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

    @PostConstruct
    public void startDiscordBot() {
        JDA jda = JDABuilder.createDefault(DISCORD_TOKEN)
                .addEventListeners(
                        new CommandManager(postService, subredditService, todoService, userService, adminUserId))
                .setActivity(Activity.playing("Type /help")).build();
        new JdaCommands().addJdaCommands(jda);
        new TestCommands().addTestCommands(jda, TEST_SERVER);
        System.out.println("Starting bot is done!");
    }

    @Bean
    public String adminUserId() {
        return adminUserId;
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