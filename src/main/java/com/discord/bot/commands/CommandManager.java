package com.discord.bot.commands;

import com.discord.bot.commands.admincommands.*;
import com.discord.bot.commands.nsfwcommands.HentaiCommand;
import com.discord.bot.commands.nsfwcommands.NsfwCommandUtils;
import com.discord.bot.commands.nsfwcommands.PornCommand;
import com.discord.bot.commands.nsfwcommands.TitsCommand;
import com.discord.bot.commands.redditcommands.*;
import com.discord.bot.commands.textcommands.*;
import com.discord.bot.commands.todocommands.*;
import com.discord.bot.service.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager extends ListenerAdapter {

    PostService postService;
    TodoService todoService;
    UserService userService;
    GuildService guildService;
    RestService restService;
    RedditCommandUtils redditCommandUtils;
    TextCommandUtils textCommandUtils;
    ToDoCommandUtils toDoCommandUtils;
    NsfwCommandUtils nsfwCommandUtils;
    private Map<String, ISlashCommand> commandsMap;

    public CommandManager(PostService postService, TodoService todoService, UserService userService,
                          GuildService guildService, RestService restService) {
        this.postService = postService;
        this.todoService = todoService;
        this.userService = userService;
        this.guildService = guildService;
        this.restService = restService;
        this.redditCommandUtils = new RedditCommandUtils(userService);
        this.textCommandUtils = new TextCommandUtils(userService);
        this.toDoCommandUtils = new ToDoCommandUtils(userService);
        this.nsfwCommandUtils = new NsfwCommandUtils(userService);
        commandMapper();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
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
        commandsMap.put("getguild", new GetGuildCommand());
        commandsMap.put("addguild", new AddGuildCommand(guildService));
        commandsMap.put("removeguild", new RemoveGuildCommand(guildService));
        commandsMap.put("getguilds", new GetGuildsCommand(guildService));
        commandsMap.put("givepermission", new GivePermissionCommand(guildService));
        commandsMap.put("retrievepermission", new RetrievePermissionCommand(guildService));
        //NSFW Commands
        commandsMap.put("hentai", new HentaiCommand(postService, nsfwCommandUtils));
        commandsMap.put("porn", new PornCommand(postService, nsfwCommandUtils));
        commandsMap.put("tits", new TitsCommand(postService, nsfwCommandUtils));
        //Reddit Commands
        commandsMap.put("blursedimages", new BlursedImagesCommand(postService, redditCommandUtils));
        commandsMap.put("dankmemes", new DankmemesCommand(postService, redditCommandUtils));
        commandsMap.put("facepalm", new FacepalmCommand(postService, redditCommandUtils));
        commandsMap.put("greentext", new GreentextCommand(postService, redditCommandUtils));
        commandsMap.put("interestingasfuck", new InterestingAFCommand(postService, redditCommandUtils));
        commandsMap.put("memes", new MemesCommand(postService, redditCommandUtils));
        commandsMap.put("perfectlycutscreams", new PCutScreamsCommand(postService, redditCommandUtils));
        commandsMap.put("unexpected", new UnexpectedCommand(postService, redditCommandUtils));
        //Text Commands
        commandsMap.put("monke", new MonkeCommand(textCommandUtils));
        commandsMap.put("howgay", new HowGayCommand(textCommandUtils));
        commandsMap.put("errrkek", new HowManCommand(textCommandUtils));
        commandsMap.put("topgg", new TopGGCommand(textCommandUtils));
        commandsMap.put("github", new GithubCommand(textCommandUtils));
        commandsMap.put("help", new HelpCommand(textCommandUtils));
        //To-do Commands
        commandsMap.put("todoadd", new ToDoAddCommand(toDoCommandUtils, todoService));
        commandsMap.put("todolist", new ToDoListCommand(toDoCommandUtils, todoService));
        commandsMap.put("todoremove", new ToDoRemoveCommand(toDoCommandUtils, todoService));
        commandsMap.put("todoupdate", new ToDoUpdateCommand(toDoCommandUtils, todoService));
        commandsMap.put("todocomplete", new ToDoCompleteCommand(toDoCommandUtils, todoService));
        commandsMap.put("todoclear", new ToDoClearCommand(toDoCommandUtils, todoService));
    }
}
