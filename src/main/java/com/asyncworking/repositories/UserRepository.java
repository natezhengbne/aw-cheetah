package com.asyncworking.repositories;

import com.asyncworking.dtos.CompanyNameDescriptionColleagueDto;
import com.asyncworking.models.ICompanyInfo;
import com.asyncworking.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u from UserEntity as u where u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query("select u from UserEntity u where u.email = :email and u.status <> 'CANCELLED'")
    Optional<UserEntity> findUserEntityByEmail(@Param("email") String email);

    @Query("select u from UserEntity as u where u.email = :email and u.status = 'UNVERIFIED'")
    Optional<UserEntity> findUnverifiedStatusByEmail(@Param("email") String email);

    @Modifying
    @Query("update UserEntity u set u.status = :status where u.email = :email")
    int updateStatusByEmail(@Param("email") String email, @Param("status") Enum status);

    @Query("select u from UserEntity u join fetch u.employees where u.email = :email")
    Optional<UserEntity> findEmploymentByEmail(@Param("email") String email);

//    @Query(nativeQuery = true, value =
//            "select ui.name from user_info ui, company_user cu " +
//                    "where ui.id = cu.user_id and cu.company_id = " +
//                    "(select cu.company_id from company_user cu, user_info ui " +
//                    "where ui.id = cu.user_id " +
//                    "and ui.email = :email )")
//    List<String> findNameByEmail(@Param("email") String email);

    @Query(nativeQuery = true, value =
            "select ui.name from user_info ui, company_user cu where ui.id = cu.user_id and cu.company_id = :id")
    List<String> findNameById(@Param("id") Long id);
}

