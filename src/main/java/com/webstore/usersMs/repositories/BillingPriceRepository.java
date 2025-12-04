package com.webstore.usersMs.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webstore.usersMs.entities.BillingPrice;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingPriceRepository extends JpaRepository<BillingPrice, Long> {

    @Query("SELECT bp FROM BillingPrice bp " +
           "LEFT JOIN FETCH bp.businessService " +
           "WHERE bp.billingPriceId = :billingPriceId")
    Optional<BillingPrice> findByBillingPriceId(@Param("billingPriceId") Long billingPriceId);

    @Query("SELECT DISTINCT bp FROM BillingPrice bp " +
           "LEFT JOIN FETCH bp.businessService " +
            "WHERE (:status IS NULL OR :status = '' OR bp.status LIKE '%' || :status || '%') " +
            "AND (:companyCompanyId IS NULL OR bp.company.companyId = :companyCompanyId) " +
            "AND (:tipoVehiculo IS NULL OR :tipoVehiculo = '' OR bp.tipoVehiculo LIKE '%' || :tipoVehiculo || '%')")
    List<BillingPrice> findBy(
            @Param("status") String status,
            @Param("companyCompanyId") Long companyCompanyId,
            @Param("tipoVehiculo") String tipoVehiculo);

    @Query(value = "SELECT DISTINCT bp FROM BillingPrice bp " +
           "LEFT JOIN FETCH bp.businessService " +
            "WHERE (:status IS NULL OR :status = '' OR bp.status LIKE '%' || :status || '%') " +
            "AND (:companyCompanyId IS NULL OR bp.company.companyId = :companyCompanyId) " +
            "AND (:tipoVehiculo IS NULL OR :tipoVehiculo = '' OR bp.tipoVehiculo LIKE '%' || :tipoVehiculo || '%')",
            countQuery = "SELECT COUNT(DISTINCT bp) FROM BillingPrice bp " +
            "WHERE (:status IS NULL OR :status = '' OR bp.status LIKE '%' || :status || '%') " +
            "AND (:companyCompanyId IS NULL OR bp.company.companyId = :companyCompanyId) " +
            "AND (:tipoVehiculo IS NULL OR :tipoVehiculo = '' OR bp.tipoVehiculo LIKE '%' || :tipoVehiculo || '%')")
    Page<BillingPrice> findByPageable(
            @Param("status") String status,
            @Param("companyCompanyId") Long companyCompanyId,
            @Param("tipoVehiculo") String tipoVehiculo,
            Pageable pageable);

    @Query("SELECT bp FROM BillingPrice bp " +
           "WHERE bp.company.companyId = :companyId " +
           "AND bp.status = 'ACTIVE' " +
           "AND :hours >= bp.start " +
           "AND :hours <= bp.end " +
           "ORDER BY bp.start ASC")
    Optional<BillingPrice> findPriceByHoursAndCompany(
            @Param("hours") Integer hours,
            @Param("companyId") Long companyId);

    @Query("SELECT bp FROM BillingPrice bp " +
           "LEFT JOIN FETCH bp.businessService " +
           "WHERE bp.company.companyId = :companyId " +
           "AND bp.status = 'ACTIVE' " +
           "AND bp.tipoVehiculo = :tipoVehiculo " +
           "AND :hours >= bp.start " +
           "AND :hours <= bp.end " +
           "ORDER BY bp.start ASC")
    Optional<BillingPrice> findPriceByHoursCompanyAndTipoVehiculo(
            @Param("hours") Integer hours,
            @Param("companyId") Long companyId,
            @Param("tipoVehiculo") String tipoVehiculo);

    @Query("SELECT MAX(bp.end) FROM BillingPrice bp " +
           "WHERE bp.company.companyId = :companyId " +
           "AND bp.status = 'ACTIVE' " +
           "AND bp.tipoVehiculo = :tipoVehiculo")
    Integer findMaxEndByCompanyAndTipoVehiculo(
            @Param("companyId") Long companyId,
            @Param("tipoVehiculo") String tipoVehiculo);

    @Query("SELECT bp FROM BillingPrice bp " +
           "WHERE bp.company.companyId = :companyId " +
           "AND bp.tipoVehiculo = :tipoVehiculo " +
           "AND (:billingPriceId IS NULL OR bp.billingPriceId != :billingPriceId) " +
           "AND ((:start BETWEEN bp.start AND bp.end) OR " +
           "     (:end BETWEEN bp.start AND bp.end) OR " +
           "     (bp.start BETWEEN :start AND :end) OR " +
           "     (bp.end BETWEEN :start AND :end))")
    List<BillingPrice> findOverlappingRanges(
            @Param("companyId") Long companyId,
            @Param("tipoVehiculo") String tipoVehiculo,
            @Param("start") Integer start,
            @Param("end") Integer end,
            @Param("billingPriceId") Long billingPriceId);
}

