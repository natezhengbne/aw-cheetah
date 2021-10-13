package com.asyncworking.repositories;

import com.asyncworking.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByCompanyIdAndId(Long companyId, Long projectId);

    List<Project> findByCompanyId(Long companyId);

    List<Project> findByCompanyIdAndIsPrivate(Long companyId, boolean isPrivate);
    @Modifying
    @Query("update Project p set p.name=:name, p.description=:description, p.updatedTime=:updatedTime " +
            "where p.id=:id and p.companyId=:companyId")
    int updateProjectInfo(@Param("id") Long id,
                          @Param("name") String name,
                          @Param("description") String description,
                          @Param("updatedTime") OffsetDateTime updatedTime,
                          @Param("companyId")Long companyId);
}
