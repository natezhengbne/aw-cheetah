package com.asyncworking.repositories;

import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.models.ICompanyInvitationEmailCompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface EmailSendRepository extends JpaRepository<EmailSendRecord, Long>{

    @Modifying
    @Query(value = "UPDATE EmailSendRecord e SET e.sendStatus = TRUE, e.receiveTime =:receiveTime " +
            "WHERE e.receiver =:email AND e.sendStatus = FALSE")
    int updateVerificationEmailSent(@NotNull @Param("email") String email,
                                    @NotNull @Param("receiveTime") OffsetDateTime receiveTime
    );

    @Query(nativeQuery = true,
            value = "select c.id, c.name as companyName, u.name as companyOwnerName \n" +
                    "from company c, user_info u \n" +
                    "where c.id = :companyId \n" +
                    "and u.id = c.admin_id ")
    Optional<ICompanyInvitationEmailCompanyInfo> findCompanyInfo(@NotNull @Param("companyId") Long companyId);

    @Modifying
    @Query(value = "UPDATE EmailSendRecord e SET e.sendStatus = TRUE, e.receiveTime =:receiveTime " +
            "WHERE e.id = :emailRecordId")
    int updateEmailRecordStatus(@NotNull @Param("emailRecordId") Long emailRecordId,
                                @NotNull @Param("receiveTime") OffsetDateTime receiveTime);
}
