package com.asyncworking.repositories;

import com.asyncworking.models.Employee;
import com.asyncworking.models.EmployeeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface EmployeeRepository extends JpaRepository<Employee, EmployeeId> {
    @Query(nativeQuery = true, value = " select company_id from company_user where user_id = :userId")
    Set<Long> findCompanyIdByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true, value =
            "SELECT COUNT (*) " +
                    "FROM company_user cu " +
                    "WHERE cu.user_id = :id " +
                    "AND cu.company_id = :companyId ")
    int findExistMemberById(@Param("id") Long id, @Param("companyId") Long companyId);
}
