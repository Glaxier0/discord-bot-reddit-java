package com.discord.bot.Entity;

import jakarta.persistence.*;
import lombok.*;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "text_count")
    private int textCount;

    @Column(name = "h_count")
    private int hCount;

    @Column(name = "p_count")
    private int pCount;

    @Column(name = "reddit_count")
    private int redditCount;

    @Column(name = "todo_count")
    private int todoCount;

    @Column(name = "user_with_tag")
    private String userWithTag;

    public User(String userId, int textCount, int hCount, int pCount, int redditCount,
                int todoCount, String userWithTag) {
        this.userId = userId;
        this.textCount = textCount;
        this.hCount = hCount;
        this.pCount = pCount;
        this.redditCount = redditCount;
        this.todoCount = todoCount;
        this.userWithTag = userWithTag;
    }
}