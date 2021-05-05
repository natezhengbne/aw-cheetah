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

@Repository
@EnableJpaRepositories
public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUserId> {

    @Query(nativeQuery = true,
            value = "select u.name, u.email \n" +
                    "from project_user pu, user_info u \n" +
                    "where pu.user_id = u.id \n" +
                    "and pu.project_id = :id" +
                    "and u.status = 'ACTIVATED' " +
                    "order by u.name")
    List<IEmployeeInfo> findAllMembersByProjectId(@Param("id") Long id);
}
