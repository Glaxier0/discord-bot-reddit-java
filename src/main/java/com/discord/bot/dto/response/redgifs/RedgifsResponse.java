package com.discord.bot.dto.response.redgifs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class RedgifsResponse {
    String page;
    String pages;
    String total;
    @JsonProperty("gifs")
    List<Redgifs> redgifs;
}
