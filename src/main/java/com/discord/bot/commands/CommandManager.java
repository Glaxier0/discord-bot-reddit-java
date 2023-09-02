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
    PostService postService;
    SubredditService subredditService;
    TodoService todoService;
    UserService userService;
    RedditCommandUtils redditCommandUtils;
    TextCommandUtils textCommandUtils;
    ToDoCommandUtils toDoCommandUtils;
    NsfwCommandUtils nsfwCommandUtils;
    private Map<String, ISlashCommand> commandsMap;

    public CommandManager(PostService postService, SubredditService subredditService,
                          TodoService todoService, UserService userService) {
        this.postService = postService;
        this.subredditService = subredditService;
        this.todoService = todoService;
        this.userService = userService;
        this.redditCommandUtils = new RedditCommandUtils(userService);
        this.textCommandUtils = new TextCommandUtils(userService);
        this.toDoCommandUtils = new ToDoCommandUtils(userService);
        this.nsfwCommandUtils = new NsfwCommandUtils(userService);
        commandMapper();
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
        commandsMap.put("guilds", new GuildsCommand());
        commandsMap.put("status", new StatusCommand(postService));
        commandsMap.put("stats", new StatsCommand(userService));
        commandsMap.put("users", new UsersCommand(userService));
        commandsMap.put("logs", new LogsCommand());
        commandsMap.put("add", new AddSubCommand(subredditService));
        commandsMap.put("list", new ListSubCommand(subredditService));
        commandsMap.put("delete", new DeleteSubCommand(subredditService));
        //NSFW Commands
        commandsMap.put("hentai", new HentaiCommand(postService, subredditService, nsfwCommandUtils));
        commandsMap.put("porn", new PornCommand(postService, subredditService, nsfwCommandUtils));
        commandsMap.put("tits", new TitsCommand(postService, subredditService, nsfwCommandUtils));
        commandsMap.put("redgifs", new RedgifsCommand(nsfwCommandUtils));
        //Reddit Commands
        commandsMap.put("blursedimages", new RedditCommand(postService, subredditService, redditCommandUtils));
        commandsMap.put("dankmemes", new RedditCommand(postService, subredditService, redditCommandUtils));
        commandsMap.put("facepalm", new RedditCommand(postService, subredditService, redditCommandUtils));
        commandsMap.put("greentext", new RedditCommand(postService, subredditService, redditCommandUtils));
        commandsMap.put("interestingasfuck", new RedditCommand(postService, subredditService, redditCommandUtils));
        commandsMap.put("memes", new RedditCommand(postService, subredditService, redditCommandUtils));
        commandsMap.put("perfectlycutscreams", new RedditCommand(postService, subredditService, redditCommandUtils));
        commandsMap.put("unexpected", new RedditCommand(postService, subredditService, redditCommandUtils));
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
}