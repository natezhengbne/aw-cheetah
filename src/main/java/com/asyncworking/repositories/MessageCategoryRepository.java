package com.asyncworking.repositories;

import com.asyncworking.models.MessageCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageCategoryRepository extends JpaRepository<MessageCategory, Long> {

    @Query(value = "select mc from MessageCategory mc left join fetch mc.project  where mc.project.id=:projectId and " +
            "mc.project.companyId=:companyId")
    List<MessageCategory> findByCompanyIdAndProjectId(@Param("companyId") Long companyId, @Param("projectId") Long projectId);
}

