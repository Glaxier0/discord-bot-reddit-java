package com.discord.bot.commands;

import com.discord.bot.commands.admincommands.*;
import com.discord.bot.commands.nsfwcommands.*;
import com.discord.bot.commands.redditcommands.*;
import com.discord.bot.commands.textcommands.*;
import com.discord.bot.commands.todocommands.*;
import com.discord.bot.service.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager extends ListenerAdapter {
    final PostService postService;
    final SubredditService subredditService;
    final TodoService todoService;
    final UserService userService;
    final RedditCommandUtils redditCommandUtils;
    final TextCommandUtils textCommandUtils;
    final ToDoCommandUtils toDoCommandUtils;
    final NsfwCommandUtils nsfwCommandUtils;
    private final String adminUserId;
    private Map<String, ISlashCommand> commandsMap;

    public CommandManager(PostService postService, SubredditService subredditService,
                          TodoService todoService, UserService userService, String adminUserId) {
        this.postService = postService;
        this.subredditService = subredditService;
        this.todoService = todoService;
        this.userService = userService;
        this.redditCommandUtils = new RedditCommandUtils(userService);
        this.textCommandUtils = new TextCommandUtils(userService);
        this.toDoCommandUtils = new ToDoCommandUtils(userService);
        this.nsfwCommandUtils = new NsfwCommandUtils(userService);
        this.adminUserId = adminUserId;
        commandMapper();
        redditCommandMapper();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();

        ISlashCommand command;
        if ((command = commandsMap.get(commandName)) != null) {
            command.execute(event);
        }
    }

    private void commandMapper() {
        commandsMap = new ConcurrentHashMap<>();
        //Admin Commands
        commandsMap.put("guilds", new GuildsCommand(adminUserId));
        commandsMap.put("status", new StatusCommand(postService, adminUserId));
        commandsMap.put("stats", new StatsCommand(userService, adminUserId));
        commandsMap.put("users", new UsersCommand(userService, adminUserId));
        commandsMap.put("logs", new LogsCommand(adminUserId));
        commandsMap.put("add", new AddSubredditCommand(subredditService, adminUserId));
        commandsMap.put("list", new ListSubredditCommand(subredditService, adminUserId));
        commandsMap.put("delete", new DeleteSubredditCommand(subredditService, adminUserId));
        //NSFW Commands
        commandsMap.put("hentai", new HentaiCommand(postService, subredditService, nsfwCommandUtils));
        commandsMap.put("porn", new PornCommand(postService, subredditService, nsfwCommandUtils));
        commandsMap.put("tits", new TitsCommand(postService, subredditService, nsfwCommandUtils));
        commandsMap.put("redgifs", new RedgifsCommand(nsfwCommandUtils));
        //Text Commands
        commandsMap.put("monke", new MonkeCommand(textCommandUtils));
        commandsMap.put("howgay", new HowGayCommand(textCommandUtils));
        commandsMap.put("errrkek", new HowManCommand(textCommandUtils));
        commandsMap.put("topgg", new TopGGCommand(textCommandUtils));
        commandsMap.put("github", new GithubCommand(textCommandUtils));
        commandsMap.put("help", new HelpCommand(textCommandUtils));
        commandsMap.put("nhelp", new NHelpCommand(textCommandUtils));
        //To-do Commands
        commandsMap.put("todoadd", new ToDoAddCommand(toDoCommandUtils, todoService));
        commandsMap.put("todolist", new ToDoListCommand(toDoCommandUtils, todoService));
        commandsMap.put("todoremove", new ToDoRemoveCommand(toDoCommandUtils, todoService));
        commandsMap.put("todoupdate", new ToDoUpdateCommand(toDoCommandUtils, todoService));
        commandsMap.put("todocomplete", new ToDoCompleteCommand(toDoCommandUtils, todoService));
        commandsMap.put("todoclear", new ToDoClearCommand(toDoCommandUtils, todoService));
    }

    private void redditCommandMapper() {
        var subreddits = subredditService.getSubredditsByGenre("reddit");
        RedditCommand redditCommand = new RedditCommand(postService, subredditService, redditCommandUtils);

        for (String subreddit : subreddits) {
            String commandName = subreddit.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
            commandsMap.put(commandName, redditCommand);
        }
    }
}