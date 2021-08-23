package com.asyncworking.repositories;

import com.asyncworking.models.EmailSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
@EnableJpaRepositories
public interface EmailSendRepository extends JpaRepository<EmailSend, Long>{

    @Modifying
    @Query(value = "UPDATE EmailSend e SET e.sendStatus = TRUE WHERE e.receiver =:email AND e.sendStatus = FALSE")
    int updateVerificationEmailSent(@NotNull @Param("email") String email);
}
