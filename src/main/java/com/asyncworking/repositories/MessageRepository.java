package com.asyncworking.repositories;

import com.asyncworking.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
@Repository
@EnableJpaRepositories
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findMessageByProjectId(Long projectId);
}
