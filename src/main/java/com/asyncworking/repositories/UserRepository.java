package com.asyncworking.repositories;

import com.asyncworking.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
//    @Query("SELECT ue.email, count(ue.email) FROM UserEntity ue")
    Optional<UserEntity> findUserEntityByEmailIgnoreCase(String email);
}
