package com.asyncworking.repositories;

import com.asyncworking.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
@EnableJpaRepositories
public interface EmailSendRepository extends JpaRepository<Project, Long>{
    @Modifying
    @Query(value = "update EmailSend e set e.isSent = true where UserEntity.email =:email", nativeQuery = true)
    int updateVerificationEmailSent(@NotNull @Param("email") String email);
}
