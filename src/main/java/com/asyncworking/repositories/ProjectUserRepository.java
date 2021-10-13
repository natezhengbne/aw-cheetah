package com.asyncworking.repositories;

import com.asyncworking.models.Project;
import com.asyncworking.models.ProjectUser;
import com.asyncworking.models.ProjectUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@EnableJpaRepositories
public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUserId> {
    @Query("select pr.project from ProjectUser pr where pr.userEntity.id = :userId")
    Set<Project> findProjectByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = " select project_id from project_user where user_id = :userId")
    Set<Long> findProjectIdByUserId(@Param("userId") Long userId);
}
