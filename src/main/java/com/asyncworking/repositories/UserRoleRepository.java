package com.asyncworking.repositories;

import com.asyncworking.models.UserRole;
import com.asyncworking.models.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    @Query(nativeQuery = true, value = " select role_id from user_role where user_id = :userId")
    Set<Long> findRoleIdByUserId(@Param("userId") Long userId);
}
