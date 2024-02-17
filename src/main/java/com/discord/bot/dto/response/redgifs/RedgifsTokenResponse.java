package com.discord.bot.dto.response.redgifs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class RedgifsTokenResponse {
    private String token;
    private String addr;
    private String agent;
    private String session;
    private String rtfm;
}
