package com.asyncworking.repositories;

import com.asyncworking.models.IMessage;
import com.asyncworking.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMessageRepository extends JpaRepository<IMessage, Long> {
    @Query(nativeQuery = true, value =
            "select u.name as poster_user, m.id, m.company_id, m.message_title , " +
                    "m.poster_user_id , m.content, m.category, m.post_time , m.doc_url as docURL " +
                    " from user_info u, message m where m.project_id= :id and m.poster_user_id = u.id"
    )
    List<IMessage> findMessageAndUserNameByProjectId(@Param("id")Long projectId);
}
