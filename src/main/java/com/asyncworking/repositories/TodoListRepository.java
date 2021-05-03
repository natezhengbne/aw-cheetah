package com.asyncworking.repositories;

import com.asyncworking.models.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface TodoListRepository extends JpaRepository<TodoList, Long> {

    @Query(nativeQuery = true, value =
            "select *\n" +
                    "from todo_list\n" +
                    "where project_id = :projectId\n" +
                    "order by created_time desc")
    List<TodoList> findTodoListsByProjectIdOrderByCreatedTime(@Param("projectId") Long projectId);
}
