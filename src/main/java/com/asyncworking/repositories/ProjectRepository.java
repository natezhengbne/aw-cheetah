package com.asyncworking.repositories;

import com.asyncworking.models.IProjectInfo;
import com.asyncworking.models.Project;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("select p.id from Project as p where p.companyId = :companyId")
    List<Long> findProjectIdsByCompanyId(@Param("companyId") Long companyId);

    @Query(nativeQuery = true, value =
            "select p.id, p.name, p.description from project p where p.id = :projectId")
    Optional<IProjectInfo> findProjectInfoByProjectId(@Param("projectId") Long projectId);

    @Query(nativeQuery = true, value =
            "select ui.name from user_info ui, project_user pu " +
                    "where ui.id = pu.user_id " +
                    "and pu.project_id = :projectId " +
                    "order by ui.name")
    List<String> findNamesByProjectId(@Param("projectId") Long projectId);

    @Query("select p from Project as p where p.id = :projectId")
    Optional<Project> findProjectByProjectId(@Param("projectId") Long projectId);

    @Modifying
    @Query("update Project p set p.name=:name, p.description=:description, p.updatedTime=:updatedTime where p.id=:id")
    int updateProjectInfo(@Param("id")Long id,
                          @Param("name") String name,
                          @Param("description") String description,
                          @Param("updatedTime") OffsetDateTime updatedTime);
}
