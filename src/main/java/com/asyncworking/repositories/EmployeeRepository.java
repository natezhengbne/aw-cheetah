package com.asyncworking.repositories;

import com.asyncworking.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    //Optional<Employee> findByUser_IdAndCompany_Id(Long userId, Long companyId);
}
