package com.discord.bot.commands;

import com.discord.bot.service.RestService;
import com.discord.bot.audioplayer.GuildMusicManager;
import com.discord.bot.audioplayer.PlayerManager;
import com.discord.bot.entity.User;
import com.discord.bot.service.GuildService;
import com.discord.bot.service.UserService;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class VoiceCommands extends ListenerAdapter {

    GuildService guildService;
    RestService restService;
    UserService userService;

    Member bot;
    GuildVoiceState botVoiceState;
    Member member;
    GuildVoiceState userVoiceState;
    GuildMusicManager musicManager;

    public VoiceCommands(GuildService guildService, RestService restService, UserService userService) {
        this.guildService = guildService;
        this.restService = restService;
        this.userService = userService;
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        bot = event.getGuild().getSelfMember();
        botVoiceState = bot.getVoiceState();
        member = event.getMember();
        userVoiceState = member.getVoiceState();
        switch (event.getName()) {
            case "play" -> play(event);
            case "skip" -> skip(event);
            case "pause" -> pause(event);
            case "resume" -> resume(event);
            case "leave" -> leave(event);
            case "queue" -> queue(event);
            case "swap" -> swap(event);
            case "shuffle" -> shuffle(event);
            case "mhelp" -> mhelp(event);
        }
    }

    private void play(SlashCommandInteractionEvent event) {
        TextChannel channel = event.getTextChannel();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());

        String youtubeLink = event.getOption("query").getAsString().trim();
        List<String> youtubeLinks = getYoutubeLink(youtubeLink, channel);

        playMusic(event, youtubeLinks);
    }

    private void skip(SlashCommandInteractionEvent event) {
        if (channelControl(botVoiceState, userVoiceState)) {
            musicManager = PlayerManager.getInstance().getMusicManager(event);
            net.dv8tion.jda.api.entities.User user = event.getUser();
            counter(user.getId(), user.getAsTag());

            musicManager.scheduler.nextTrack();
            event.replyEmbeds(new EmbedBuilder().setDescription("Song skipped").setColor(Color.GREEN).build()).queue();
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Please be in a same voice channel as bot.")
                    .setColor(Color.RED).build()).queue();
        }
    }

    private void pause(SlashCommandInteractionEvent event) {
        if (channelControl(botVoiceState, userVoiceState)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            musicManager = PlayerManager.getInstance().getMusicManager(event);
            net.dv8tion.jda.api.entities.User user = event.getUser();
            counter(user.getId(), user.getAsTag());

            musicManager.audioPlayer.setPaused(true);
            event.replyEmbeds(embedBuilder.setDescription("Song paused")
                    .setColor(Color.GREEN).build()).queue();
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Please be in a same voice channel as bot.")
                    .setColor(Color.RED).build()).queue();
        }
    }

    private void resume(SlashCommandInteractionEvent event) {
        if (channelControl(botVoiceState, userVoiceState)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            musicManager = PlayerManager.getInstance().getMusicManager(event);
            net.dv8tion.jda.api.entities.User user = event.getUser();
            counter(user.getId(), user.getAsTag());

            musicManager.audioPlayer.setPaused(false);
            event.replyEmbeds(embedBuilder.setDescription("Song resumed")
                    .setColor(Color.GREEN).build()).queue();
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Please be in a same voice channel as bot.")
                    .setColor(Color.RED).build()).queue();
        }
    }

    private void leave(SlashCommandInteractionEvent event) {
        if (channelControl(botVoiceState, userVoiceState)) {
            musicManager = PlayerManager.getInstance().getMusicManager(event);
            AudioManager audioManager = event.getGuild().getAudioManager();
            net.dv8tion.jda.api.entities.User user = event.getUser();
            counter(user.getId(), user.getAsTag());

            musicManager.scheduler.player.stopTrack();
            musicManager.scheduler.queue.clear();
            audioManager.closeAudioConnection();
            event.replyEmbeds(new EmbedBuilder().setDescription("Bye.").build()).queue();
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Please be in a same voice channel as bot.")
                    .setColor(Color.RED).build()).queue();
        }
    }

    private void queue(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        musicManager = PlayerManager.getInstance().getMusicManager(event);
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());

        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        int trackCount = Math.min(queue.size(), 20);
        List<AudioTrack> trackList = new ArrayList<>(queue);

        if (queue.isEmpty()) {
            embedBuilder.setDescription("The queue is currently empty").setColor(Color.RED);
            event.replyEmbeds(embedBuilder.build()).queue();
            return;
        }

        embedBuilder.setTitle("Current Queue:");
        for (int i = 0; i < trackCount; i++) {
            AudioTrack track = trackList.get(i);
            AudioTrackInfo info = track.getInfo();
            embedBuilder.appendDescription((i + 1) + ". " + info.title + "\n");
        }

        if (trackList.size() > trackCount) {
            embedBuilder.appendDescription("And " + (trackList.size() - trackCount) + " more...");
        }

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void swap(SlashCommandInteractionEvent event) {
        if (channelControl(botVoiceState, userVoiceState)) {
            musicManager = PlayerManager.getInstance().getMusicManager(event);
            net.dv8tion.jda.api.entities.User user = event.getUser();
            counter(user.getId(), user.getAsTag());

            List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);

            if (trackList.size() > 1) {
                int first = event.getOption("songnum1").getAsInt() - 1;
                int second = event.getOption("songnum2").getAsInt() - 1;

                try {
                    AudioTrack temp = trackList.get(first);
                    trackList.set(first, trackList.get(second));
                    trackList.set(second, temp);
                } catch (Exception e) {
                    event.replyEmbeds(new EmbedBuilder()
                            .setDescription("Please enter a valid queue ids for both of the songs.")
                            .setColor(Color.RED).build()).queue();
                    return;
                }

                musicManager.scheduler.queue.clear();
                for (AudioTrack track : trackList) {
                    musicManager.scheduler.queue(track);
                }

                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("Successfully swapped order of two songs")
                        .setColor(Color.GREEN).build()).queue();
            } else if (trackList.size() == 1) {
                event.replyEmbeds(new EmbedBuilder().setDescription("There is only one song in queue.")
                        .setColor(Color.RED).build()).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder().setDescription("Queue is empty.")
                        .setColor(Color.RED).build()).queue();
            }
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Please be in a same voice channel as bot.")
                    .setColor(Color.RED).build()).queue();
        }
    }

    private void shuffle(SlashCommandInteractionEvent event) {
        if (channelControl(botVoiceState, userVoiceState)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            musicManager = PlayerManager.getInstance().getMusicManager(event);
            net.dv8tion.jda.api.entities.User user = event.getUser();
            counter(user.getId(), user.getAsTag());

            List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
            if (trackList.size() > 1) {
                Collections.shuffle(trackList);
                musicManager.scheduler.queue.clear();

                for (AudioTrack track : trackList) {
                    musicManager.scheduler.queue(track);
                }

                embedBuilder.setDescription("Queue shuffled").setColor(Color.GREEN);
            } else {
                embedBuilder.setDescription("Queue size have to be at least two.").setColor(Color.RED);
            }
            event.replyEmbeds(embedBuilder.build()).queue();
        } else {
            event.replyEmbeds(new EmbedBuilder().setDescription("Please be in a same voice channel as bot.")
                    .setColor(Color.RED).build()).queue();
        }
    }

    private void mhelp(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        counter(user.getId(), user.getAsTag());

        embedBuilder.setTitle("Music Commands").setDescription("""
                        - /play
                        - /skip
                        - /pause
                        - /resume
                        - /leave
                        - /queue
                        - /swap
                        - /shuffle
                        """)
                .setFooter("Bot can't play age restricted videos and shorts.");
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private boolean channelControl(GuildVoiceState selfVoiceState, GuildVoiceState memberVoiceState) {

        if (!selfVoiceState.inAudioChannel()) {
            return false;
        }

        if (!memberVoiceState.inAudioChannel()) {
            return false;
        }

        return memberVoiceState.getChannel() == selfVoiceState.getChannel();
    }

    private void counter(String userId, String userWithTag) {
        User user = userService.getUser(userId);
        if (user == null) {
            user = new User(userId, 0, 0, 0, 0, 0, userWithTag, 0);
        }
        user.setMusicCount(user.getMusicCount() + 1);
        userService.save(user);
    }

    private void playMusic(SlashCommandInteractionEvent event, List<String> youtubeLinks) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        AudioChannel userChannel = event.getMember().getVoiceState().getChannel();
        AudioChannel botChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
        boolean isUserInVoiceChannel = event.getMember().getVoiceState().inAudioChannel();
        boolean isBotInVoiceChannel = event.getGuild().getSelfMember().getVoiceState().inAudioChannel();
        if (isUserInVoiceChannel && !youtubeLinks.isEmpty()) {
            if (!isBotInVoiceChannel) {
                musicManager = PlayerManager.getInstance().getMusicManager(event);
                musicManager.scheduler.player.destroy();
                musicManager.scheduler.queue.clear();
                audioManager.openAudioConnection(userChannel);
                botChannel = userChannel;
            }
            if (userChannel.equals(botChannel)) {
                int trackSize = youtubeLinks.size();
                if (trackSize > 1) {
                    PlayerManager.getInstance().loadMultipleAndPlay(event, youtubeLinks);
                } else if (trackSize == 1) {
                    PlayerManager.getInstance().loadAndPlay(event, youtubeLinks.get(0));
                }
            }
        }
    }

    private ArrayList<String> getYoutubeLink(String query, MessageChannel channel) {
        ArrayList<String> youtubeLinks = new ArrayList<>();
        if (query.contains("https://www.youtube.com/watch?v=")) {
            youtubeLinks.add(query);
        } else if (query.contains("https://open.spotify.com/")) {
            channel.sendMessageEmbeds(new EmbedBuilder().setDescription("Spotify links are not supported.")
                    .setColor(Color.RED).build()).queue();
//            youtubeLinks = spotifyToYoutube(query);
//
//            if (youtubeLinks.get(youtubeLinks.size() - 1).equals("403glaxierror")) {
//                youtubeLinks.remove(youtubeLinks.size() - 1);
//                apiLimitExceeded(channel);
//            }
        } else {
            String youtubeLink = restService.getYoutubeLink(query);
            if (youtubeLink.equals("403glaxierror")) {
                apiLimitExceeded(channel);
            } else {
                youtubeLinks.add(youtubeLink);
            }
        }

        return youtubeLinks;
    }

    private ArrayList<String> spotifyToYoutube(String spotifyUrl) {
        return restService.getSpotifyMusicName(spotifyUrl);
    }

    private void apiLimitExceeded(MessageChannel channel) {
        channel.sendMessageEmbeds(new EmbedBuilder().setDescription("Youtube quota has exceeded. " +
                "Please use youtube links to play music for today.").build()).queue();
    }
}
