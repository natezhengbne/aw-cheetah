package com.asyncworking.repositories;

import com.asyncworking.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByCompanyIdAndProjectId(Long companyId, Long projectId);
    Optional<Message> findByCompanyIdAndProjectIdAndId(Long companyId, Long projectId, Long Id);
}
