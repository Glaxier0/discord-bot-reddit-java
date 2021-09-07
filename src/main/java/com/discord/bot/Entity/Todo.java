package com.discord.bot.Entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "todo")
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

    public Todo() {

    }
    public Todo(String discordUser, String todoRow, Date created) {
        this.discordUser = discordUser;
        this.todoRow = todoRow;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiscordUser() {
        return discordUser;
    }

    public void setDiscordUser(String discordUser) {
        this.discordUser = discordUser;
    }

    public String getTodoRow() {
        return todoRow;
    }

    public void setTodoRow(String todoRow) {
        this.todoRow = todoRow;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", discordUser='" + discordUser + '\'' +
                ", todoRow='" + todoRow + '\'' +
                ", created=" + created +
                '}';
    }
}
