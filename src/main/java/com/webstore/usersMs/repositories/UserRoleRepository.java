package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webstore.usersMs.entities.UserRole;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT r FROM UserRole r WHERE r.role = :userRoleId")
    Optional<UserRole> findUserRole(@Param("userRoleId") Long userRoleId);

    @Query("SELECT r FROM UserRole r WHERE r.user.appUserId = :userId")
    List<UserRole> findByUser(Long userId);

    @Query("SELECT r FROM UserRole r WHERE r.user.firstName = :userName")
    List<UserRole> findByUserName(@Param("userName") String  userName);
}
