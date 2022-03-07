package com.discord.bot.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "guilds")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Guild {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "guild_id")
    private String guildId;

    @Column(name = "guild_name")
    private String guildName;

    public Guild(String guildId, String guildName) {
        this.guildId = guildId;
        this.guildName = guildName;
    }
}
