package com.asyncworking.repositories;

import com.asyncworking.models.IEmployeeInfo;
import com.asyncworking.models.Employee;
import com.asyncworking.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u from UserEntity as u where u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query("select u from UserEntity u where u.email = :email and u.status <> 'CANCELLED'")
    Optional<UserEntity> findUserEntityByEmail(@Param("email") String email);

    @Query("select u from UserEntity as u where u.email = :email and u.status = 'UNVERIFIED'")
    Optional<UserEntity> findUnverifiedStatusByEmail(@Param("email") String email);

    @Query("select u from UserEntity as u where u.id = :id and u.status <> 'CANCELLED'")
    Optional<UserEntity> findUserEntityById(@Param("id") Long id);

    @Modifying
    @Query("update UserEntity u set u.status = :status where u.email = :email")
    int updateStatusByEmail(@Param("email") String email, @Param("status") Enum status);

    @Query("select u from UserEntity u join fetch u.employees where u.email = :email")
    Optional<UserEntity> findEmploymentByEmail(@Param("email") String email);

    @Query(nativeQuery = true,
        value = "select u.name, u.email, cu.title \n" +
                "from company_user cu, user_info u \n" +
                "where cu.user_id = u.id \n" +
                "and cu.company_id = :id " +
                "and u.status = 'ACTIVATED' " +
                "order by u.name")
    List<IEmployeeInfo> findAllEmployeeByCompanyId(@Param("id") Long id);

    @Query(nativeQuery = true,
            value = "select u.name, u.email, cu.title " +
                    "from project_user pu, user_info u, company_user cu " +
                    "where pu.user_id = u.id " +
                    "and cu.user_id = u.id " +
                    "and pu.project_id = :id " +
                    "and u.status = 'ACTIVATED' " +
                    "order by u.name")
    List<IEmployeeInfo> findAllMembersByProjectId(@Param("id") Long id);

    @Query(nativeQuery = true,
            value = "SELECT u.id, u.name, u.email, userinfo.title FROM (\n" +
                    "    SELECT * FROM awcheetah.company_user cu \n" +
                    "\t\tWHERE cu.user_id NOT IN (\n" +
                    "\t\t\tSELECT projectu.user_id FROM (\n" +
                    "\t\t\t\tSELECT * FROM awcheetah.project_user pu \n" +
                    "\t\t\t\t\tWHERE pu.project_id = :projectId) AS projectu) \n" +
                    "    AND cu.company_id = :companyId) AS userinfo \n" +
                    "LEFT JOIN awcheetah.user_info u ON u.id = userinfo.user_id AND u.status = 'ACTIVATED'\n" +
                    "ORDER BY u.name;")
    List<IEmployeeInfo> findNonMembersEmployeesByCompanyAndProjectId(@Param("companyId") Long companyId,
                                                                    @Param("projectId") Long projectId);

    @Query("select e from Employee e join fetch e.userEntity u where u.email = :email")
    List<Employee> findEmployeesByEmail(@Param("email") String email);

    Optional<List<UserEntity>> findByIdIn(List<Long> id);

    boolean existsById(Long id);
}
