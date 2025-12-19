package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.UserPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPrinterRepository extends JpaRepository<UserPrinter, Long> {

    Optional<UserPrinter> findByUserPrinterId(Long userPrinterId);

    @Query("SELECT up FROM UserPrinter up " +
            "WHERE (:userPrinterId IS NULL OR :userPrinterId = up.userPrinterId) " +
            "AND (:userUserId IS NULL OR up.user.appUserId = :userUserId) " +
            "AND (:printerPrinterId IS NULL OR up.printer.printerId = :printerPrinterId) " +
            "AND (:isActive IS NULL OR up.isActive = :isActive)")
    List<UserPrinter> findBy(
            @Param("userPrinterId") Long userPrinterId,
            @Param("userUserId") Long userUserId,
            @Param("printerPrinterId") Long printerPrinterId,
            @Param("isActive") Boolean isActive);

    @Query(value = "SELECT up FROM UserPrinter up " +
            "WHERE (:userPrinterId IS NULL OR :userPrinterId = up.userPrinterId) " +
            "AND (:userUserId IS NULL OR up.user.appUserId = :userUserId) " +
            "AND (:printerPrinterId IS NULL OR up.printer.printerId = :printerPrinterId) " +
            "AND (:isActive IS NULL OR up.isActive = :isActive)",
            countQuery = "SELECT COUNT(up) FROM UserPrinter up " +
            "WHERE (:userPrinterId IS NULL OR :userPrinterId = up.userPrinterId) " +
            "AND (:userUserId IS NULL OR up.user.appUserId = :userUserId) " +
            "AND (:printerPrinterId IS NULL OR up.printer.printerId = :printerPrinterId) " +
            "AND (:isActive IS NULL OR up.isActive = :isActive)")
    Page<UserPrinter> findByPageable(
            @Param("userPrinterId") Long userPrinterId,
            @Param("userUserId") Long userUserId,
            @Param("printerPrinterId") Long printerPrinterId,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    // Buscar por usuario y impresora específica
    Optional<UserPrinter> findByUserAppUserIdAndPrinterPrinterId(Long userId, Long printerId);

}

