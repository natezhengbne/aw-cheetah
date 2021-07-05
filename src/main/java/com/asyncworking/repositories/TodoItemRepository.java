package com.asyncworking.repositories;

import com.asyncworking.models.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;


@Repository
@EnableJpaRepositories
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    List<TodoItem> findByTodoListIdOrderByCreatedTimeDesc(@Param("todoListId") Long todoListId);


    @Modifying
    @Query(value = "update TodoItem t set t.description=:description,t.notes=:notes,t.originNotes=:originNotes," +
            "t.dueDate=:dueDates where t.id=:todoItemId")
    int updateTodoItem(@Param("todoItemId") Long todoItemId,
                       @Param("description")String description,
                       @Param("notes")String notes,
                       @Param("originNotes")String originNotes,
                       @Param("dueDates")OffsetDateTime dueDate);
}
