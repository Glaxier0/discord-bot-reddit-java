package com.discord.bot.Service;

import com.discord.bot.Entity.Post;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RemoveOldPosts {
    PostService postService;

    public void removeOldPosts() {
        System.out.println("Program in remove old posts.");

        List<Post> posts = postService.getOldPosts();
        System.out.println("Post count to be deleted: " + posts.size());

        for (Post post : posts) {
            postService.delete(post);
        }
        System.out.println("Deleting old posts done!");
    }
}
