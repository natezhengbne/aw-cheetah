package com.asyncworking.repositories;

import com.asyncworking.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByEmailIgnoreCase(String email);

    boolean existsUserEntityByEmailEquals(String email);

    @Query("SELECT ue FROM UserEntity ue WHERE ue.email = 'email'")
    List<UserEntity> findAllByEmailEquals(String email);

    @Query("select u from UserEntity as u where u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);
}
