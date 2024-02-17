package com.discord.bot.Entity;

import lombok.*;
import jakarta.persistence.*;
import java.sql.Date;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "todo")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "discord_user")
    private String discordUser;

    @Column(name = "todo_row", length = 1024)
    private String todoRow;

    @Column(name = "created")
    private Date created;

    @Column(name = "is_completed")
    private boolean isCompleted;

    @Column(name = "user_with_tag")
    private String userWithTag;

    public Todo(String discordUser, String todoRow, Date created, boolean isCompleted, String userWithTag) {
        this.discordUser = discordUser;
        this.todoRow = todoRow;
        this.created = created;
        this.isCompleted = isCompleted;
        this.userWithTag = userWithTag;
    }
}