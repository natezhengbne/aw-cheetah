package com.asyncworking.repositories;

import com.asyncworking.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /*@Query(value = "SELECT * FROM (SELECT * FROM company c " +
            "JOIN employee e ON c.id = e.company_id " +
            "JOIN user u ON e.user_id = u.id) " +
            "WHERE u.email = :email",  nativeQuery = true)
    List<Company> findCompaniesByUserEmail(@Param("email") String email);
*/
    @Query(value = "SELECT * FROM company c " +
            "JOIN employee e ON c.id = e.company_id " +
            "WHERE e.user_id = :userId", nativeQuery = true)
    List<Company> findCompaniesByUserId(@Param("userId") Long userId);


}
