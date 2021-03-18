package com.asyncworking.repositories;

import com.asyncworking.dtos.CompanyNameDescriptionColleagueDto;
import com.asyncworking.dtos.CompanyNameDescriptionColleagueDto;

import com.asyncworking.models.Company;
import com.asyncworking.models.UserEntity;
import com.asyncworking.models.ICompanyInfo;
import com.asyncworking.models.UserEntity;
import com.asyncworking.models.ICompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.OffsetDateTime;
import java.util.Date;

import java.util.Optional;

import java.util.Optional;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query(nativeQuery = true, value =
            "SELECT c.id, c.name, c.description FROM company c, company_user cu, user_info u WHERE \n" +
                    "c.id = cu.company_id AND \n" +
                    "cu.user_id = u.id AND \n" +
                    "u.email = :email")
    @Query(nativeQuery = true, value =
            "SELECT c.id, c.name, c.description FROM company c, company_user cu, user_info u WHERE \n" +
                    "c.id = cu.company_id AND \n" +
                    "cu.user_id = u.id AND \n" +
                    "u.email = :email")
    List<ICompanyInfo> findCompanyInfoByEmail(@Param("email") String email);
    @Query("select u from UserEntity u where u.email=:email")
    Optional<Company> findCompanyInfoByEmail(@Param("email") String email);
    @Query("select u from UserEntity u where u.email=:email")
    Optional<Company> findCompanyInfoByEmail(@Param("email") String email);
    Optional<List<ICompanyInfo>> findCompanyInfoByEmail(@Param("email") String email);


    @Modifying
    @Query("update Company c  set c.name=:name, c.description=:description, c.updatedTime=:updatedTime where c.id=:id")
    int updateCompanyProfileById(
            @Param("name") String name,
            @Param("description") String description,
            @Param("updatedTime") Date updatedTime,
            @Param("id")Long id);
}