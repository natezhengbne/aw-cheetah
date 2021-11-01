package com.asyncworking.repositories;

import com.asyncworking.models.MessageCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface MessageCategoryRepository extends JpaRepository<MessageCategory, Long> {

    @Query(value = "select mc from MessageCategory mc left join fetch mc.project  where mc.project.id=:projectId and " +
            "mc.project.companyId=:companyId")
    List<MessageCategory> findByCompanyIdAndProjectId(@Param("companyId") Long companyId, @Param("projectId") Long projectId);


    Optional<MessageCategory> findById(@NotNull long along);

    @Modifying
    @Query(value = "update MessageCategory mc set mc.categoryName=:name, mc.emoji=:emoji WHERE mc.id=:id")
    int editMessage(@Param("id") Long id, @Param("name") String name, @Param("emoji") String emoji);

}

