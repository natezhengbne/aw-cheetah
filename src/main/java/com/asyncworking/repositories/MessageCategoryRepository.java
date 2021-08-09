package com.asyncworking.repositories;

import com.asyncworking.models.MessageCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface MessageCategoryRepository extends JpaRepository<MessageCategory, Long> {

    List<MessageCategory> findByCompanyIdAndProjectId(Long companyId, Long projectId);
}

