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
import java.util.Optional;


@Repository
@EnableJpaRepositories
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    List<TodoItem> findByCompanyIdAndProjectIdAndTodoListIdOrderByCreatedTimeDesc(Long companyId, Long projectId,
                                                                                  @Param("todoListId") Long todoListId);
    Optional<TodoItem> findByCompanyIdAndProjectIdAndId(Long companyId, Long projectId, Long id);


    @Modifying
    @Query(value = "update TodoItem t set t.description=:description,t.notes=:notes,t.originNotes=:originNotes," +
            "t.dueDate=:dueDates, t.subscribersIds=:subscribersIds where t.id=:todoItemId and t.companyId=:companyId " +
            "and t.projectId = :projectId")
    int updateTodoItem(@Param("todoItemId") Long todoItemId,
                       @Param("description")String description,
                       @Param("notes")String notes,
                       @Param("originNotes")String originNotes,
                       @Param("dueDates")OffsetDateTime dueDate,
                       @Param("companyId")Long companyId,
                       @Param("projectId")Long projectId,
                       @Param("subscribersIds")String subscribersIds);

    @Query(value = "select t.subscribersIds from TodoItem t where t.id = :todoItemId and t.projectId = :projectId")
    String findSubscribersIdsByProjectIdAndId(@Param("projectId")Long projectId, @Param("todoItemId") Long todoItemId);
}
