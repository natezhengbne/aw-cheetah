package com.asyncworking.repositories;

import com.asyncworking.models.MessageBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface MessageBoardRepository extends JpaRepository<MessageBoard, Long> {
}
