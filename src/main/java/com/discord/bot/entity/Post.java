package com.discord.bot.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "posts")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "url")
    private String url;

    @Column(name = "subreddit")
    private String subreddit;

    @Column(name = "title", length = 512)
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "created")
    private Date created;

    @Column(name = "type")
    private String contentType;

    @Column(name = "firebase_url")
    private String firebaseUrl;

    @Column(name = "perma_url")
    private String permaUrl;

    @Column(name = "download_url", length = 512)
    private String downloadUrl;

    public Post(String url, String subreddit, String title, String author, Date created, String permalink) {
        this.url = url;
        this.subreddit = subreddit;
        this.title = title;
        this.author = author;
        this.created = created;
        this.permaUrl = permalink;
    }
}