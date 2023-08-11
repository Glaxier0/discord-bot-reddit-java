package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.service.PostService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class StatusCommand implements ISlashCommand {
    PostService postService;
    String ADMIN = "your_discord_id";

    public StatusCommand(PostService postService) {
        this.postService = postService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            int videoCount = postService.getVideoNullFirebase().size();
            List<String> subredditPostCount = postService.getSubredditCount();

            subredditPostCount.replaceAll(s -> s.replace(",", ": "));

            embedBuilder.setTitle("Status")
                    .setDescription("Video count to be uploaded: " + videoCount)
                    .addField("Subreddit post counts", subredditPostCount.toString()
                            .replace("[", "")
                            .replace("]", "")
                            .replace(",", "\n"), true);
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }
}
