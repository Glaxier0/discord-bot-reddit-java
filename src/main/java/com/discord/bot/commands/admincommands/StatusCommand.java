package com.discord.bot.commands.admincommands;

import com.discord.bot.commands.ISlashCommand;
import com.discord.bot.Service.PostService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

@AllArgsConstructor
public class StatusCommand implements ISlashCommand {
    final PostService postService;
    private final String adminUserId;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().getId().equals(adminUserId)) {
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
