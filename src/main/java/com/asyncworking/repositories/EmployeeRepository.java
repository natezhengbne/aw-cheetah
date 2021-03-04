package com.asyncworking.repositories;

import com.asyncworking.models.Employee;
import com.asyncworking.models.EmployeeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface EmployeeRepository extends JpaRepository<Employee, EmployeeId> {

    @Query(value = "select * from Employee where user_id = :userId and company_id = :companyId", nativeQuery = true)
    Optional<Employee> findByUser_IdAndCompany_Id(@Param("userId") Long userId, @Param("companyId") Long companyId);

    @Query(value = "select * from Employee where user_id = :userId", nativeQuery = true)
    List<Employee> findByUserId(@Param("userId") Long userId);
}
