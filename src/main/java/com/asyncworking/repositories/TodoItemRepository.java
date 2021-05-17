package com.asyncworking.repositories;

import com.asyncworking.models.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@EnableJpaRepositories
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    @Query(nativeQuery = true, value =
            "select *\n" +
                    "from todo_item\n" +
                    "where todo_list_id = :todoListId\n" +
                    "order by created_time desc")
    List<TodoItem> findTodoItemListByTodoListIdOrderByCreatedTime(@Param("todoListId") Long todoListId);
}
