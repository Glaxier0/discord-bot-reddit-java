package com.discord.bot.Dao;

import com.discord.bot.Entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer>, TodoRepositoryCustom {

}
