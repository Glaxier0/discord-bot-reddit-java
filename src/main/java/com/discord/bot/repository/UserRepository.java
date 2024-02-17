package com.discord.bot.repository;

import com.discord.bot.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM users ORDER BY (text_count + h_count + p_count + reddit_count + todo_count) DESC", nativeQuery = true)
    List<User> getUsers();
}