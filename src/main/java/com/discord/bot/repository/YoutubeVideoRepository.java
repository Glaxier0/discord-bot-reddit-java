package com.discord.bot.repository;

import com.discord.bot.entity.YoutubeVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YoutubeVideoRepository extends JpaRepository<YoutubeVideo, Integer> {
    YoutubeVideo getVideoByUrl(String url);
    @Query(value = "SELECT s.url FROM YoutubeVideo s WHERE s.genre = :genre")
    List<String> getYoutubeVideoUrlsByGenre(String genre);
}