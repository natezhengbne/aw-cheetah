package com.asyncworking.repositories;

import com.asyncworking.models.IMessage;
import com.asyncworking.models.IMessageInfo;
import com.asyncworking.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(nativeQuery = true, value =
            "select u.name as poster_user, m.id, m.company_id, m.message_title as messageTitle , " +
                    "m.poster_user_id as posterUserId, m.content, m.category, m.post_time as postTime, m.doc_url as docURL " +
                    " from user_info u, message m where m.project_id= :id and m.poster_user_id = u.id"
    )
    List<IMessageInfo> findMessageAndUserNameByProjectId(@Param("id")Long projectId);

}
