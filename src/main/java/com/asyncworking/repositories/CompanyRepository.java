package com.asyncworking.repositories;

import com.asyncworking.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface CompanyRepository extends JpaRepository<Company, Long> {

}
