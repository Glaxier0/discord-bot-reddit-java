package com.discord.bot.dao;

import com.discord.bot.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> , PostRepositoryCustom {

}
