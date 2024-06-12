package com.discord.bot.service;

import com.discord.bot.entity.YoutubeVideo;
import com.discord.bot.repository.YoutubeVideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class YoutubeVideoServiceImpl implements YoutubeVideoService {
    YoutubeVideoRepository repository;

    @Override
    public List<YoutubeVideo> findAll() {
        return repository.findAll();
    }

    @Override
    public void save(YoutubeVideo youtubeVideo) {
        repository.save(youtubeVideo);
    }

    @Override
    public void delete(YoutubeVideo youtubeVideo) {
        repository.delete(youtubeVideo);
    }

    @Override
    public YoutubeVideo getYoutubeVideo(String url) {
        return repository.getVideoByUrl(url);
    }

    @Override
    public List<YoutubeVideo> getYoutubeVideos() {
        return repository.findAll();
    }

    @Override
    public List<String> getYoutubeVideosByGenre(String genre) {
        return repository.getYoutubeVideoUrlsByGenre(genre);
    }
}