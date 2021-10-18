package com.asyncworking.repositories;

import com.asyncworking.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByProjectId(Long projectId);

    @Query(nativeQuery = true,
            value = "select exists" +
                    "(select * from message " +
                    "where company_id = :companyId " +
                    "and project_id = :projectId " +
                    "and id = :messageId);")
    Boolean findIfMessageExists(@Param("companyId") Long companyId, @Param("projectId") Long projectId, @Param("messageId") Long messageId);
    List<Message> findByCompanyIdAndProjectId(Long companyId, Long projectId);
    Optional<Message> findByCompanyIdAndProjectIdAndId(Long companyId, Long projectId, Long id);
}
