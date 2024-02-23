# Discord Bot for Reddit using Java

This is a versatile Discord bot primarily designed for Reddit, capable of fetching posts from Reddit and posting them in Discord channels.

## Used APIs ##

[Reddit API](https://www.reddit.com/dev/api/)

[Firebase Storage API](https://console.firebase.google.com/)

Firebase Storage allows direct embedding of Reddit videos in Discord. Alternatively, any video hosting site can be used.

Discord API - [JDA Wrapper](https://github.com/DV8FromTheWorld/JDA)

# Requirements for Bot Setup #

## Reddit ##

Create reddit personal use script [here](https://www.reddit.com/prefs/apps) to obtain `client_id` and `client_secret`.

Obtain a Reddit refresh token following [these instructions](https://github.com/reddit-archive/reddit/wiki/OAuth2).

After getting reddit environments edit the [application.yaml](https://github.com/Glaxier0/discord-bot-reddit-java/blob/4ae350a1d9a1ef2bb6738b8d98c5a84d71edd535/src/main/resources/application.yaml#L17).

## Firebase ##

Obtain the Firebase Storage ADMIN SDK KEY from [here](https://console.firebase.google.com/)

From the Firebase console (your project > settings > users and permissions > service accounts), generate a private key and download it then add it to the project root.

Also from (settings > general) note your `Project ID` and append `.appspot.com` to end of it, it should be something like this.

```
your-project-id.appspot.com
```

Edit bucket name and file name in [application.yaml](https://github.com/Glaxier0/discord-bot-reddit-java/blob/4ae350a1d9a1ef2bb6738b8d98c5a84d71edd535/src/main/resources/application.yaml#L34).

Example yaml:
```
firebase:
  storage:
    bucket:
      name: your-project-id.appspot.com
  adminsdk:
    file:
      name: your-admin-sdk.json
```

## Discord ##

Obtain the Discord API bot token from [here](https://discord.com/developers/applications).

Find the ID of your main/admin server (only admin commands can be used in this server).

Find the ID of your Discord user (this determines who the bot admin is).

If you're unsure how to find IDs, [here's a guide]().

After obtaining all necessary environment details, edit [application.yaml](https://github.com/Glaxier0/discord-bot-reddit-java/blob/4ae350a1d9a1ef2bb6738b8d98c5a84d71edd535/src/main/resources/application.yaml#L28).


### How to find id of the server and user ###

First you need to turn on developer mode in the discord app.

User settings > under app settings > advanced > turn on developer mode.

Now by right clicking any server or user you can get id of them.

## Database ##

Simply use the Docker Compose:

```
docker compose up
```

If you don't want to use docker then you should create a database called `reddit_bot` manually.

Thanks to Spring Boot JPA it will auto generate tables when the bot first run.


[Example bot created by me](https://discord.com/api/oauth2/authorize?client_id=863361433807093792&permissions=139586889792&scope=bot%20applications.commands)

[Check out the bot on top.gg](https://top.gg/bot/855806720834928641)

### PREVIEW ###

https://user-images.githubusercontent.com/55876415/133401787-96db0fea-80a7-4b94-b211-3812e4c635c9.mp4
