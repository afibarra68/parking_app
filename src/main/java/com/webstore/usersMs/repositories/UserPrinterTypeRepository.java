package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.UserPrinterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPrinterTypeRepository extends JpaRepository<UserPrinterType, Long> {

    Optional<UserPrinterType> findByUserPrinterTypeId(Long userPrinterTypeId);

    @Query("SELECT upt FROM UserPrinterType upt " +
            "WHERE (:userPrinterTypeId IS NULL OR :userPrinterTypeId = upt.userPrinterTypeId) " +
            "AND (:userUserId IS NULL OR upt.user.appUserId = :userUserId) " +
            "AND (:printerType IS NULL OR :printerType = '' OR upt.printerType = :printerType) " +
            "AND (:isEnabled IS NULL OR upt.isEnabled = :isEnabled)")
    List<UserPrinterType> findBy(
            @Param("userPrinterTypeId") Long userPrinterTypeId,
            @Param("userUserId") Long userUserId,
            @Param("printerType") String printerType,
            @Param("isEnabled") Boolean isEnabled);

    @Query(value = "SELECT upt FROM UserPrinterType upt " +
            "WHERE (:userPrinterTypeId IS NULL OR :userPrinterTypeId = upt.userPrinterTypeId) " +
            "AND (:userUserId IS NULL OR upt.user.appUserId = :userUserId) " +
            "AND (:printerType IS NULL OR :printerType = '' OR upt.printerType = :printerType) " +
            "AND (:isEnabled IS NULL OR upt.isEnabled = :isEnabled)",
            countQuery = "SELECT COUNT(upt) FROM UserPrinterType upt " +
            "WHERE (:userPrinterTypeId IS NULL OR :userPrinterTypeId = upt.userPrinterTypeId) " +
            "AND (:userUserId IS NULL OR upt.user.appUserId = :userUserId) " +
            "AND (:printerType IS NULL OR :printerType = '' OR upt.printerType = :printerType) " +
            "AND (:isEnabled IS NULL OR upt.isEnabled = :isEnabled)")
    Page<UserPrinterType> findByPageable(
            @Param("userPrinterTypeId") Long userPrinterTypeId,
            @Param("userUserId") Long userUserId,
            @Param("printerType") String printerType,
            @Param("isEnabled") Boolean isEnabled,
            Pageable pageable);

    // Buscar por usuario y tipo de impresora
    Optional<UserPrinterType> findByUserAppUserIdAndPrinterType(Long userId, String printerType);

}

