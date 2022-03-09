package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.service.PostService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class StatusCommand implements ISlashCommand {
    PostService postService;
    String ADMIN = "315403352496275456";

    public StatusCommand(PostService postService) {
        this.postService = postService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(ADMIN)) {
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
    }
}
