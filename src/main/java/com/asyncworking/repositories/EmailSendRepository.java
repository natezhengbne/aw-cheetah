package com.asyncworking.repositories;

import com.asyncworking.models.EmailSendRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Repository
@EnableJpaRepositories
public interface EmailSendRepository extends JpaRepository<EmailSendRecord, Long>{

    @Modifying
    @Query(value = "UPDATE EmailSendRecord e SET e.sendStatus = TRUE, e.receiveTime =:receiveTime " +
            "WHERE e.receiver =:email AND e.sendStatus = FALSE")
    int updateVerificationEmailSent(@NotNull @Param("email") String email,
                                    @NotNull @Param("receiveTime") OffsetDateTime receiveTime
    );
}
