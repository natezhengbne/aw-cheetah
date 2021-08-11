package com.asyncworking.repositories;

import com.asyncworking.models.Employee;
import com.asyncworking.models.EmployeeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@EnableJpaRepositories
public interface EmployeeRepository extends JpaRepository<Employee, EmployeeId> {
    @Query(nativeQuery = true, value = " select company_id from company_user where user_id = :userId")
    Set<Long> findCompanyIdByUserId(@Param("userId") Long userId);
}
