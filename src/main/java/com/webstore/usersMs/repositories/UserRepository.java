package com.webstore.usersMs.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webstore.usersMs.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNumberIdentity(String identity);

    Optional<User> findByAppUserId(Long appUserId);

    @Query("SELECT u FROM User u " +
           "WHERE (:appUserId IS NULL OR :appUserId = u.appUserId) " +
           "AND (:numberIdentity IS NULL OR :numberIdentity = '' OR u.numberIdentity LIKE '%' || :numberIdentity || '%') " +
           "AND (:companyCompanyId IS NULL OR :companyCompanyId = u.companyCompanyId)")
    Page<User> findByPageable(
            @Param("appUserId") Long appUserId,
            @Param("numberIdentity") String numberIdentity,
            @Param("companyCompanyId") Long companyCompanyId,
            Pageable pageable);

}
