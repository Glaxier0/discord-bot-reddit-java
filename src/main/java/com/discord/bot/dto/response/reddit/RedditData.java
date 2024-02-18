package com.discord.bot.dto.response.reddit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@SuppressWarnings("unused")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class RedditData {
    private List<Children> children;
}
