package com.asyncworking.repositories;

import com.asyncworking.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByNameOrEmail(String name, String email);
    User findByEmailLike(String email);
}
