# discord-bot-reddit-java

A multi-functional discord bot mainly focused to reddit that gathers posts from reddit and post them in discord channel.
### Used APIs ###
[Reddit API](https://www.reddit.com/dev/api/)

[Firebase Storage API](https://console.firebase.google.com/)

Firebase Storage let us watch reddit videos directly in discord via embed. Any video hosting site can be used instead of Firebase Storage.

Discord API - [JDA Wrapper](https://github.com/DV8FromTheWorld/JDA)

### Things needed for project to work ###

Edit admin ids in classes under [admincommands](https://github.com/Glaxier0/discord-bot-reddit-java/tree/main/src/main/java/com/discord/bot/commands/admincommands)

Also don't forget to edit [application.properties](https://github.com/Glaxier0/discord-bot-reddit-java/blob/main/src/main/resources/application.properties).

Reddit username, reddit password

Create reddit personal use script from [here](https://www.reddit.com/prefs/apps) than get Reddit client_id and client_secret.

Get reddit refresh token following [this](https://github.com/reddit-archive/reddit/wiki/OAuth2)

Firebase Storage ADMIN SDK KEY from [here](https://console.firebase.google.com/)

from Firebase console > your project > settings > users and permissions > service accounts > generate private key > download the key.

Copy key path then edit this [line](https://github.com/Glaxier0/discord-bot-reddit-java/blob/8a78ef0a4e6fa12b1f5c9381860c2312e84bbc37/src/main/resources/application.properties#L11)

Discord API BOT TOKEN from [here](https://discord.com/developers/applications)

An sql database named reddit_bot

reddit_bot database needs posts table.

[Table create script for postgresql](https://github.com/Glaxier0/discord-bot-reddit-java/blob/main/postgresql-create-script.md)

Some error may occur while creating table from script so an SQL knowledge needed.

[Example bot created by me](https://discord.com/api/oauth2/authorize?client_id=863361433807093792&permissions=139586889792&scope=bot%20applications.commands)

[top.gg](https://top.gg/bot/855806720834928641)

### PREVIEW ###

https://user-images.githubusercontent.com/55876415/133401787-96db0fea-80a7-4b94-b211-3812e4c635c9.mp4











