package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.Printer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrinterRepository extends JpaRepository<Printer, Long> {

    Optional<Printer> findByPrinterId(Long printerId);

    @Query("SELECT p FROM Printer p " +
            "WHERE (:printerId IS NULL OR :printerId = p.printerId) " +
            "AND (:printerName IS NULL OR :printerName = '' OR LOWER(p.printerName) LIKE '%' || LOWER(:printerName) || '%') " +
            "AND (:printerType IS NULL OR :printerType = '' OR p.printerType = :printerType) " +
            "AND (:companyCompanyId IS NULL OR p.company.companyId = :companyCompanyId) " +
            "AND (:userUserId IS NULL OR p.user.appUserId = :userUserId) " +
            "AND (:isActive IS NULL OR p.isActive = :isActive)")
    List<Printer> findBy(
            @Param("printerId") Long printerId,
            @Param("printerName") String printerName,
            @Param("printerType") String printerType,
            @Param("companyCompanyId") Long companyCompanyId,
            @Param("userUserId") Long userUserId,
            @Param("isActive") Boolean isActive);

    @Query(value = "SELECT p FROM Printer p " +
            "WHERE (:printerId IS NULL OR :printerId = p.printerId) " +
            "AND (:printerName IS NULL OR :printerName = '' OR LOWER(p.printerName) LIKE '%' || LOWER(:printerName) || '%') " +
            "AND (:printerType IS NULL OR :printerType = '' OR p.printerType = :printerType) " +
            "AND (:companyCompanyId IS NULL OR p.company.companyId = :companyCompanyId) " +
            "AND (:userUserId IS NULL OR p.user.appUserId = :userUserId) " +
            "AND (:isActive IS NULL OR p.isActive = :isActive)",
            countQuery = "SELECT COUNT(p) FROM Printer p " +
            "WHERE (:printerId IS NULL OR :printerId = p.printerId) " +
            "AND (:printerName IS NULL OR :printerName = '' OR LOWER(p.printerName) LIKE '%' || LOWER(:printerName) || '%') " +
            "AND (:printerType IS NULL OR :printerType = '' OR p.printerType = :printerType) " +
            "AND (:companyCompanyId IS NULL OR p.company.companyId = :companyCompanyId) " +
            "AND (:userUserId IS NULL OR p.user.appUserId = :userUserId) " +
            "AND (:isActive IS NULL OR p.isActive = :isActive)")
    Page<Printer> findByPageable(
            @Param("printerId") Long printerId,
            @Param("printerName") String printerName,
            @Param("printerType") String printerType,
            @Param("companyCompanyId") Long companyCompanyId,
            @Param("userUserId") Long userUserId,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

}

