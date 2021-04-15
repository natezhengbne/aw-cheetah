package com.asyncworking.repositories;

import com.asyncworking.models.ProjectUser;
import com.asyncworking.models.ProjectUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUserId> {
}
