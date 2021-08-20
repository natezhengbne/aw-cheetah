package com.asyncworking.repositories;

import com.asyncworking.models.Role;
import com.asyncworking.models.UserEntity;
import com.asyncworking.models.UserRole;
import com.asyncworking.models.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    @Query("select ur.role from UserRole ur where ur.userEntity.id = :userId")
    Set<Role> findRoleSetByUserId(@Param("userId") Long userId);

    Set<UserRole> findByUserEntity(UserEntity userEntity);
}
