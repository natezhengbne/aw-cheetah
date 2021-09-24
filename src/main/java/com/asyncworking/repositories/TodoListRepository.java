package com.asyncworking.repositories;

import com.asyncworking.models.TodoList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface TodoListRepository extends JpaRepository<TodoList, Long> {

    @Query(nativeQuery = true, value =
            "select *\n" +
                    "from todo_list\n" +
                    "where project_id = :projectId\n" +
                    "order by created_time desc" +
                    " limit :quantity")
    List<TodoList> findTodoListsByProjectIdOrderByCreatedTime(@Param("projectId") Long projectId, @Param("quantity") Integer quantity);

    @Query(value = "select tl from TodoList tl left join fetch tl.todoItems where tl.project.id=:projectId and " +
            "tl.companyId=:companyId order by tl.createdTime desc")
    List<TodoList> findTodolistWithTodoItems(@Param("companyId") long companyId, @Param("projectId") Long projectId, Pageable pageable);

    Optional<TodoList> findByCompanyIdAndProjectIdAndId(Long companyId, Long projectId, Long todoListId);

    @Modifying
    @Query("update TodoList t set t.todoListTitle=:title, t.details=:detail, t.updatedTime=:updatedTime " +
            "where t.id=:id and t.companyId=:companyId and t.project.id=:projectId")
    int updateTodoListInfo(
            @Param("id") Long id,
            @Param("companyId")Long companyId,
            @Param("projectId")Long projectId,
            @Param("title") String title,
            @Param("detail") String detail,
            @Param("updatedTime") OffsetDateTime updatedTime
    );
}
