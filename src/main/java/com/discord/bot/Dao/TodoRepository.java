package com.discord.bot.dao;

import com.discord.bot.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer>, TodoRepositoryCustom {
}