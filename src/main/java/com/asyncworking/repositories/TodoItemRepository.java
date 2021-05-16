package com.asyncworking.repositories;

import com.asyncworking.models.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;



@Repository
@EnableJpaRepositories
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
}
