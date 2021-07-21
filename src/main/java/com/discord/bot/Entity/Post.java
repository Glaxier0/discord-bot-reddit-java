package com.discord.bot.Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "posts")
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

    @Column(name = "download_url", length = 512)
    private String downloadUrl;

    @Column(name = "vimeo_url")
    private String vimeoUrl;

    @Column(name = "perma_url")
    private String permaUrl;

    public Post() {

    }

    public Post(String url, String subreddit, String title, String author, Date created) {
        this.url = url;
        this.subreddit = subreddit;
        this.title = title;
        this.author = author;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String links) {
        this.url = links;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getVimeoUrl() {
        return vimeoUrl;
    }

    public void setVimeoUrl(String vimeoUrl) {
        this.vimeoUrl = vimeoUrl;
    }

    public String getPermaUrl() {
        return permaUrl;
    }

    public void setPermaUrl(String permaUrl) {
        this.permaUrl = permaUrl;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", link='" + url + '\'' +
                ", subreddit='" + subreddit + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", created=" + created +
                ", contentType='" + contentType + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", vimeoUrl='" + vimeoUrl + '\'' +
                ", permaUrl='" + permaUrl + '\'' +
                '}';
    }
}
