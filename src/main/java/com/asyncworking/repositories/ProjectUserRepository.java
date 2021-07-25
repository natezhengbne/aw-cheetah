package com.asyncworking.repositories;

import com.asyncworking.models.IEmployeeInfo;
import com.asyncworking.models.ProjectUser;
import com.asyncworking.models.ProjectUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@EnableJpaRepositories
public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUserId> {
//    @Query(nativeQuery = true, value =" select project_id from project_user where user_id = :userId")
//    List<Long> findProjectIdByUserId(@Param("userId") Long userId);
}
