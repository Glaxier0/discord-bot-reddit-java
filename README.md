# discord-bot-reddit-java

A multi-functional discord bot mainly focused to reddit that gathers posts from reddit and post them in discord channel, [now with music commands!](https://github.com/Glaxier0/discord-bot-reddit-java#music-commands).

### Used APIs ###
Reddit API - [JRAW Wrapper](https://github.com/mattbdean/JRAW)

[Firebase Storage API](https://console.firebase.google.com/)

Firebase Storage let us watch reddit videos directly in discord via embed. Any video hosting site can be used instead of Firebase Storage.

Discord API - [JDA Wrapper](https://github.com/DV8FromTheWorld/JDA)

[Youtube API](https://developers.google.com/youtube/v3)


[Spotify API](https://developer.spotify.com/documentation/web-api/)

### Things needed for project to work ###

Don't forget to edit [application.properties](https://github.com/Glaxier0/discord-bot-reddit-java/blob/main/src/main/resources/application.properties).

Reddit username, reddit password, reddit personal use script from [here](https://www.reddit.com/prefs/apps)
Reddit client_id, client_secret.

Firebase Storage ADMIN SDK KEY from [here](https://console.firebase.google.com/)

from Firebase console > your project > settings > users and permissions > service accounts > generate private key > download the key.

Copy key path then edit this lines [[1]](https://github.com/Glaxier0/discord-bot-reddit-java/blob/7ac2606c0bc97d621ebc4eb2f11a97cc8201d6a6/src/main/java/com/discord/bot/Service/UploadToFirebase.java#L19)
[[2]](https://github.com/Glaxier0/discord-bot-reddit-java/blob/7ac2606c0bc97d621ebc4eb2f11a97cc8201d6a6/src/main/java/com/discord/bot/Service/RemoveOldPosts.java#L36)

Discord API BOT TOKEN from [here](https://discord.com/developers/applications)

An sql database named reddit_bot

reddit_bot database needs posts table.

[Table create script for postgresql](https://github.com/Glaxier0/discord-bot-java/blob/master/postgresql-create-script.md)

Some error may occur while creating table from script so an sql knowledge needed.

[Example bot created by me](https://discord.com/api/oauth2/authorize?client_id=855806720834928641&permissions=139623589952&scope=bot%20applications.commands)

[top.gg](https://top.gg/bot/855806720834928641)

### PREVIEW ###

https://user-images.githubusercontent.com/55876415/133401787-96db0fea-80a7-4b94-b211-3812e4c635c9.mp4

# MUSIC COMMANDS 

### DON'T FORGET TO ADD YOURSELF AS ADMIN, TO DO SO GET YOUR DISCORD USER ID AND ADD IT TO THE [admin commands](https://github.com/Glaxier0/discord-bot-reddit-java/tree/main/src/main/java/com/discord/bot/commands/admincommands)

### ALSO DON'T FORGET TO EDIT YOUR TEST SERVER ID AND ADD IT TO THE [application.properties](https://github.com/Glaxier0/discord-bot-reddit-java/blob/12be07f1dcabd12f65370a29975bf42f568b8dfd/src/main/resources/application.properties#L9). EVERY COMMAND USABLE IN TEST SERVER

If you want to give permission to any discord server for music commands (because i made them as premium feature).

First you have to type /getguild for getting server id or /guilds and find server id there.

Second type /addpermission guildid guildname command to give permission to discord server

Now music commands will open in that server. If you want to take permission back use /retrievepermission guildid command.










