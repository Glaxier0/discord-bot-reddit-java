# discord-bot-reddit-java
 A multi-functional discord bot mainly focused to reddit that gathers posts from reddit and post them in discord channel.
 
### Used APIs ###
Reddit API - [JRAW Wrapper](https://github.com/mattbdean/JRAW)

[Firebase Storage API](https://console.firebase.google.com/)

Firebase Storage let us watch reddit videos directly in discord via embed. Any video hosting site can be used instead of Firebase Storage.

Discord API - [JDA Wrapper](https://github.com/DV8FromTheWorld/JDA)

### Things needed for project to work ###

Don't forget to edit [application.properties](https://github.com/Glaxier0/discord-bot-reddit-java/blob/main/src/main/resources/application.properties).

Reddit username, reddit password, reddit personal use script from [here](https://www.reddit.com/prefs/apps) 
Reddit client_id, client_secret.

Firebase Storage ADMIN SDK KEY from [here](https://console.firebase.google.com/) 

from Firebase console > your project > settings > users and permissions > service accounts > generate private key > download the key.

Copy key path then edit this lines [[1]](https://github.com/Glaxier0/discord-bot-reddit-java/blob/c847cbb908ebb0bce6a540bbe50bcbe38cdd5fbd/src/main/java/com/discord/bot/Bot.java#L206)
[[2]](https://github.com/Glaxier0/discord-bot-reddit-java/blob/c847cbb908ebb0bce6a540bbe50bcbe38cdd5fbd/src/main/java/com/discord/bot/Bot.java#L247)

Discord API BOT TOKEN from [here](https://discord.com/developers/applications)

An sql database named reddit_bot

reddit_bot database needs posts table.

[Table create script for postgresql](https://github.com/Glaxier0/discord-bot-java/blob/master/postgresql-create-script.md)

Some error may occur while creating table from script so an sql knowledge needed.

[Example bot created by me](https://discord.com/api/oauth2/authorize?client_id=855806720834928641&permissions=2147936320&scope=bot)

[top.gg](https://top.gg/bot/855806720834928641)

### PREVIEW ###

https://user-images.githubusercontent.com/55876415/133401787-96db0fea-80a7-4b94-b211-3812e4c635c9.mp4







