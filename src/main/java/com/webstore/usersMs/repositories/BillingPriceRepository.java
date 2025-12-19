package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.enums.EBillingStatus;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webstore.usersMs.entities.BillingPrice;

import java.time.LocalDate;
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
                        "WHERE (:status IS NULL OR bp.status = :status) " +
                        "AND (:companyCompanyId IS NULL OR bp.company.companyId = :companyCompanyId) " +
                        "AND (:tipoVehiculo IS NULL OR bp.tipoVehiculo = :tipoVehiculo)")
        List<BillingPrice> findBy(
                        @Param("status") EBillingStatus status,
                        @Param("companyCompanyId") Long companyCompanyId,
                        @Param("tipoVehiculo") ETipoVehiculo tipoVehiculo);

        @Query(value = "SELECT bp FROM BillingPrice bp " +
                        "LEFT JOIN FETCH bp.businessService " +
                        "WHERE (:status IS NULL OR bp.status = :status) " +
                        "AND (:companyCompanyId IS NULL OR bp.company.companyId = :companyCompanyId) " +
                        "AND (:tipoVehiculo IS NULL OR bp.tipoVehiculo = :tipoVehiculo)"
        )
        Page<BillingPrice> findByPageable(
                        @Param("status") EBillingStatus status,
                        @Param("companyCompanyId") Long companyCompanyId,
                        @Param("tipoVehiculo") ETipoVehiculo tipoVehiculo,
                        Pageable pageable);

        @Query("SELECT bp FROM BillingPrice bp " +
                        "WHERE bp.company.companyId = :companyId " +
                        "AND bp.status = :status " +
                        "AND :hours >= bp.start " +
                        "AND :hours <= bp.end " +
                        "ORDER BY bp.start ASC")
        Optional<BillingPrice> findPriceByHoursAndCompany(
                        @Param("hours") Integer hours,
                        @Param("companyId") Long companyId,
                        @Param("status") EBillingStatus status);

        @Query("SELECT bp FROM BillingPrice bp " +
                        "LEFT JOIN FETCH bp.businessService " +
                        "WHERE bp.company.companyId = :companyId " +
                        "AND bp.status = :status " +
                        "AND bp.tipoVehiculo = :tipoVehiculo " +
                        "AND bp.hours = :hours")
        Optional<BillingPrice> findPriceByHoursCompanyAndTipoVehiculo(
                        @Param("hours") Integer hours,
                        @Param("companyId") Long companyId,
                        @Param("tipoVehiculo") ETipoVehiculo tipoVehiculo,
                        @Param("status") EBillingStatus status);

        @Query("SELECT bp FROM BillingPrice bp " +
                        "LEFT JOIN FETCH bp.businessService " +
                        "WHERE bp.company.companyId = :companyId " +
                        "AND bp.status = :status " +
                        "AND bp.tipoVehiculo = :tipoVehiculo " +
                        "AND bp.hours <= :hours " +
                        "ORDER BY bp.hours DESC")
        List<BillingPrice> findPriceByHoursLessOrEqual(
                        @Param("hours") Integer hours,
                        @Param("companyId") Long companyId,
                        @Param("tipoVehiculo") ETipoVehiculo tipoVehiculo,
                        @Param("status") EBillingStatus status);

        @Query("SELECT MAX(bp.hours) FROM BillingPrice bp " +
                        "WHERE bp.company.companyId = :companyId " +
                        "AND bp.status = :status " +
                        "AND bp.tipoVehiculo = :tipoVehiculo")
        Integer findMaxHoursByCompanyAndTipoVehiculo(
                        @Param("companyId") Long companyId,
                        @Param("tipoVehiculo") ETipoVehiculo tipoVehiculo,
                        @Param("status") EBillingStatus status);

        @Deprecated
        @Query("SELECT MAX(bp.end) FROM BillingPrice bp " +
                        "WHERE bp.company.companyId = :companyId " +
                        "AND bp.status = :status " +
                        "AND bp.tipoVehiculo = :tipoVehiculo")
        Integer findMaxEndByCompanyAndTipoVehiculo(
                        @Param("companyId") Long companyId,
                        @Param("tipoVehiculo") ETipoVehiculo tipoVehiculo,
                        @Param("status") EBillingStatus status);

        @Query("SELECT bp FROM BillingPrice bp " +
                        "WHERE bp.company.companyId = :companyId " +
                        "AND bp.tipoVehiculo = :tipoVehiculo " +
                        "AND (:billingPriceId IS NULL OR bp.billingPriceId != :billingPriceId) " +
                        "AND bp.hours = :hours")
        List<BillingPrice> findDuplicateHours(
                        @Param("companyId") Long companyId,
                        @Param("tipoVehiculo") ETipoVehiculo tipoVehiculo,
                        @Param("hours") Integer hours,
                        @Param("billingPriceId") Long billingPriceId);

        /**
         * Obtiene el precio por hora para un tipo de vehículo específico.
         * Busca la tarifa de 1 hora para ese tipo de vehículo, ya que el precio por
         * hora
         * debería ser constante independientemente de las horas transcurridas.
         * 
         * @param companyId    ID de la compañía
         * @param tipoVehiculo Tipo de vehículo
         * @return Optional con la tarifa de 1 hora, o vacío si no se encuentra
         */
        @Query("SELECT bp FROM BillingPrice bp " +
                        "LEFT JOIN FETCH bp.businessService " +
                        "WHERE bp.company.companyId = :companyId " +
                        "AND bp.status = :status " +
                        "AND bp.tipoVehiculo = :tipoVehiculo " +
                        "AND (bp.dateStartDisabled IS NULL OR bp.dateStartDisabled > :currentDate) " +
                        "ORDER BY bp.billingPriceId ASC")
        List<BillingPrice> findPricePerHourByTipoVehiculo(
                        @Param("companyId") Long companyId,
                        @Param("tipoVehiculo") ETipoVehiculo tipoVehiculo,
                        @Param("status") EBillingStatus status,
                        @Param("currentDate") LocalDate currentDate);

        @Deprecated
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
