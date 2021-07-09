# discord-bot-java
 A discord bot project that gathers posts from reddit and post them in discord channel.
 
### Used APIs ###
Reddit API - [JRAW Wrapper](https://github.com/mattbdean/JRAW)

Vimeo API - [clickntap Java SDK](https://github.com/clickntap/Vimeo) 

Vimeo API let us watch reddit videos directly in discord via embed. Any video hosting site can be used instead of Vimeo

Keep in mind vimeo has free 10 daily video upload quota (if you use vimeo, plus upgrade required for bot to work 24/7). 

Youtube has free 6 daily video upload quota so good luck finding good one.

Discord API - [JDA Wrapper](https://github.com/DV8FromTheWorld/JDA)

### Things needed for project to work ###

Reddit username, reddit password, reddit personal use script from https://www.reddit.com/prefs/apps 
Reddit client_id, client_secret.

Vimeo API TOKEN from https://developer.vimeo.com/apps

DISCORD API BOT TOKEN from https://discord.com/developers/applications

An sql database named reddit_bot (if you use different sql than postgresql hibernate.cfg.xml needs to be edited.)

reddit_bot database needs posts table.

[Table create script for postgresql](https://github.com/celiktahir/discord-bot-java/blob/master/postgresql-create-script.md)

Some error may occur while creating table from script so an sql knowledge needed.




