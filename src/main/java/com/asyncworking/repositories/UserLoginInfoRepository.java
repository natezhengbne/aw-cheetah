package com.asyncworking.repositories;

import com.asyncworking.models.UserLoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserLoginInfoRepository extends JpaRepository<UserLoginInfo, Long> {
    @Modifying
    @Query("update UserLoginInfo as u set u.companyId = :companyId, u.updatedTime = :loginTime where u.userId = :userId ")
    void setUserLoginCompanyId(@Param("companyId") Long companyId,
                               @Param("userId") Long userId,
                               @Param("loginTime") OffsetDateTime loginTime);

    @Query("select u.companyId from UserLoginInfo as u where u.userId = :userId")
    Long findUserLoginCompanyIdByUserId(@Param("userId") Long userId);

    Optional<UserLoginInfo> findUserLoginInfoByUserId(Long userId);
}
