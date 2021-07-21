package com.discord.bot.Dao;

import com.discord.bot.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> , PostRepositoryCustom {

}
