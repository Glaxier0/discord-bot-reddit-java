package com.discord.bot;

import com.discord.bot.commands.CommandManager;
import com.discord.bot.commands.JdaCommands;
import com.discord.bot.commands.AdminCommands;
import com.discord.bot.service.*;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class Bot {
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    final TodoService todoService;
    final UserService userService;
    final PostService postService;
    final SubredditService subredditService;
    final YoutubeVideoService youtubeVideoService;

    final SearchReddit redditSearchService;
    final RemoveOldPosts removeOldPostsService;
    final FirebaseServiceImpl firebaseService;
    final RedditTokenService redditTokenService;
    final RedgifsTokenService redgifsTokenService;

    @Value("${discord.bot.token}")
    private String discordToken;

    @Value("${discord.admin.server.id}")
    private String adminServerId;

    @Value("${discord.admin.user.id}")
    private String adminUserId;

    public Bot(PostService postService, SubredditService subredditService,
               TodoService todoService, UserService userService,
               YoutubeVideoService youtubeVideoService, SearchReddit redditSearchService,
               RedditTokenService redditTokenService, RemoveOldPosts removeOldPostsService,
               FirebaseServiceImpl firebaseService, RedgifsTokenService redgifsTokenService) {
        this.postService = postService;
        this.subredditService = subredditService;
        this.todoService = todoService;
        this.userService = userService;
        this.youtubeVideoService = youtubeVideoService;
        this.redditSearchService = redditSearchService;
        this.redditTokenService = redditTokenService;
        this.removeOldPostsService = removeOldPostsService;
        this.firebaseService = firebaseService;
        this.redgifsTokenService = redgifsTokenService;
    }

    @PostConstruct
    public void startDiscordBot() throws InterruptedException {
        JDA jda = JDABuilder.createDefault(discordToken)
                .addEventListeners(
                        new CommandManager(postService, subredditService, youtubeVideoService, todoService, userService, adminUserId))
                .setActivity(Activity.playing("Type /help")).build();
        jda.awaitReady();
        new JdaCommands(subredditService).addJdaCommands(jda);
        new AdminCommands().addAdminCommands(jda, adminServerId);
        logger.info("Starting bot is done!");
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