package com.asyncworking.repositories;

import com.asyncworking.models.Company;
import com.asyncworking.models.ICompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findCompanyById(@Param("id") Long id);

    @Query(nativeQuery = true, value =
            "SELECT c.id, c.name, c.description FROM company c, company_user cu, user_info u WHERE \n" +
                    "c.id = cu.company_id AND \n" +
                    "cu.user_id = u.id AND \n" +
                    "u.email = :email")
    List<ICompanyInfo> findCompanyInfoByEmail(@Param("email") String email);

    @Modifying
    @Query("update Company c  set c.name=:name, c.description=:description, c.updatedTime=:updatedTime where c.id=:id")
    int updateCompanyProfileById(@Param("id")Long id,
            @Param("name") String name,
            @Param("description") String description,
            @Param("updatedTime") OffsetDateTime updatedTime);

    @Query(nativeQuery = true, value =
            "select ui.name from user_info ui, company_user cu " +
                    "where ui.id = cu.user_id " +
                    "and cu.company_id = :id " +
                    "and ui.status = 'ACTIVATED' " +
                    "order by ui.name " +
                    "LIMIT 6")
    List<String> findNameById(@Param("id") Long id);

    boolean existsById(Long id);
}
