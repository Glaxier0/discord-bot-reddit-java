package com.discord.bot.service;

import com.discord.bot.entity.YoutubeVideo;

import java.util.List;

@SuppressWarnings("unused")
public interface YoutubeVideoService {
    List<YoutubeVideo> findAll();

    void save(YoutubeVideo youtubeVideo);

    void delete(YoutubeVideo youtubeVideo);

    YoutubeVideo getYoutubeVideo(String url);

    List<YoutubeVideo> getYoutubeVideos();

    List<String> getYoutubeVideosByGenre(String genre);
}
