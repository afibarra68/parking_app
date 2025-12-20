package com.webstore.usersMs.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webstore.usersMs.entities.OpenTransaction;

import java.util.Optional;

@Repository
public interface OpenTransactionRepository extends JpaRepository<OpenTransaction, Long> {

    Optional<OpenTransaction> findByOpenTransactionIdAndCompanyCompanyId(Long openTransactionId, Long companyId);

    Optional<OpenTransaction> findByOpenTransactionId(Long openTransactionId);

    @Query(value = "SELECT ot.* FROM open_transaction ot " +
           "WHERE (:status IS NULL OR :status = '' OR ot.status ILIKE '%' || :status || '%') " +
           "AND (:companyCompanyId IS NULL OR ot.company_company_id = :companyCompanyId)",
           countQuery = "SELECT COUNT(*) FROM open_transaction ot " +
           "WHERE (:status IS NULL OR :status = '' OR ot.status ILIKE '%' || :status || '%') " +
           "AND (:companyCompanyId IS NULL OR ot.company_company_id = :companyCompanyId)",
           nativeQuery = true)
    Page<OpenTransaction> findBy(
            @Param("status") String status,
            @Param("companyCompanyId") Long companyCompanyId,
            Pageable pageable);

    @Query("SELECT ot FROM OpenTransaction ot " +
           "WHERE ot.vehiclePlate = :vehiclePlate " +
           "AND ot.company.companyId = :companyId " +
           "AND ot.status = 'OPEN'")
    Optional<OpenTransaction> findByVehiclePlateAndCompanyId(
            @Param("vehiclePlate") String vehiclePlate,
            @Param("companyId") Long companyId);
}

