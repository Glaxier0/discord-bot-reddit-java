package com.discord.bot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "youtube_videos")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class YoutubeVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "url")
    private String url;

    @Column(name = "genre")
    private String genre;

    public YoutubeVideo(String url, String genre) {
        this.url = url;
        this.genre = genre;
    }
}
