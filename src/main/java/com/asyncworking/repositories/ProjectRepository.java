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
import java.util.Set;

@Repository
@EnableJpaRepositories
public interface ProjectRepository extends JpaRepository<Project, Long> {


    List<Project> findProjectsByCompanyId(@Param("companyId") Long companyId);

    @Query(nativeQuery = true, value = "select id from project where company_id = :companyId")
    Set<Long> findProjectIdSetByCompanyId(@Param("companyId") Long companyId);

    @Query(nativeQuery = true, value = "select id from project where company_id = :companyId and is_private = false")
    Set<Long> findPublicProjectIdSetByCompanyId(@Param("companyId") Long companyId);

    @Modifying
    @Query("update Project p set p.name=:name, p.description=:description, p.updatedTime=:updatedTime where p.id=:id")
    int updateProjectInfo(@Param("id") Long id,
                          @Param("name") String name,
                          @Param("description") String description,
                          @Param("updatedTime") OffsetDateTime updatedTime);
}
