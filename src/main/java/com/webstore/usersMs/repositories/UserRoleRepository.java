package com.webstore.usersMs.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webstore.usersMs.entities.UserRole;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends PagingAndSortingRepository<UserRole, Long> {

    UserRole save(UserRole client);

    @Query("SELECT r FROM UserRole r WHERE r.role = :userRoleId")
    Optional<UserRole> findUserRole(@Param("userRoleId") Long userRoleId);

    @Query("SELECT r FROM UserRole r WHERE r.user.appUserId = :userId")
    List<UserRole> findByUser  (Long userId);

    @Query("SELECT r FROM UserRole r WHERE r.user.firstName = :userName")
    List<UserRole> findByUserName(@Param("userName") String  userName);
}
