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

    List<Message> findByProjectId(@Param("id")Long projectId);

}
