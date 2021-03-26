package com.asyncworking.repositories;


import com.asyncworking.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Date;

@Repository
@EnableJpaRepositories
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Modifying
    @Query("update Company c  set c.name=:name, c.description=:description, c.updatedTime=:updatedTime where c.id=:id")
    int updateCompanyProfileById(
            @Param("name") String name,
            @Param("description") String description,
            @Param("updatedTime") Date updatedTime,
            @Param("id")Long id);

}
