package com.asyncworking.repositories;

import com.asyncworking.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u from UserEntity as u where u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query("select u from UserEntity u where u.email = :email and u.status <> 'CANCELLED'")
    Optional<UserEntity> findUserEntityByEmail(@Param("email") String email);
}
