package com.example.DiscordBot.Listener;

import com.example.DiscordBot.Entity.Post;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Random;


/**
 * Discord bot listener class
 */
public class EventListener extends ListenerAdapter {

    SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Post.class)
            .buildSessionFactory();

    public Session getFactory() {

        if(factory.isClosed()) {
            SessionFactory factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Post.class)
                    .buildSessionFactory();
            return factory.getCurrentSession();
        }
        else {
            return factory.getCurrentSession();
        }
    }

    /**
     * Listens discord guild messages
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        Random random = new Random();
        String messageSent = event.getMessage().getContentRaw();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        //if user types !Unexpected get subreddit post information from database and prints them to discord.
        if (messageSent.equalsIgnoreCase("!Unexpected") && !(event.getMember().getUser().isBot())) {

            Session session = getFactory();
            session.beginTransaction();

            Query query = session.createQuery("SELECT e FROM Post e WHERE e.contentType IS NOT NULL " +
                    "AND e.subreddit = 'Unexpected'");

            List<Post> list = query.getResultList();

            session.getTransaction().commit();
            session.close();

            Post post = list.get(random.nextInt(list.size()));

            if (post.getContentType().equals("video")) {

                while (post.getVimeoUrl() == null && (post.getContentType().equals("video"))) {
                    post = list.get(random.nextInt(list.size()));
                }

                event.getChannel().sendMessage(post.getVimeoUrl()).queue();
            }

            else if (post.getContentType().equals("gif") && post.getContentType().equals("image")) {

                embedBuilder.setTitle(post.getTitle(), post.getPermaUrl())
                        .setImage(post.getUrl())
                        .setFooter("Posted in r/" + post.getSubreddit() + " by u/" + post.getAuthor());

                event.getChannel().sendMessage(embedBuilder.build()).queue();

            }

            else if (post.getContentType().equals("text")) {

                event.getChannel().sendMessage(post.getPermaUrl()).queue();

            }
        }
    }
}

