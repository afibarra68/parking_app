package com.webstore.usersMs.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webstore.usersMs.entities.ClosedTransaction;

import java.util.Optional;

@Repository
public interface ClosedTransactionRepository extends JpaRepository<ClosedTransaction, Long> {

    Optional<ClosedTransaction> findByClosedTransactionId(Long closedTransactionId);

    @Query(value = "SELECT ct.* FROM closed_transaction ct " +
           "WHERE (:status IS NULL OR :status = '' OR ct.status ILIKE '%' || :status || '%') " +
           "AND (:companyCompanyId IS NULL OR ct.company_company_id = :companyCompanyId) " +
           "AND (:operationDateFrom IS NULL OR DATE(ct.operation_date) >= CAST(:operationDateFrom AS DATE)) " +
           "AND (:operationDateTo IS NULL OR DATE(ct.operation_date) < CAST(:operationDateTo AS DATE))",
           countQuery = "SELECT COUNT(*) FROM closed_transaction ct " +
           "WHERE (:status IS NULL OR :status = '' OR ct.status ILIKE '%' || :status || '%') " +
           "AND (:companyCompanyId IS NULL OR ct.company_company_id = :companyCompanyId) " +
           "AND (:operationDateFrom IS NULL OR DATE(ct.operation_date) >= CAST(:operationDateFrom AS DATE)) " +
           "AND (:operationDateTo IS NULL OR DATE(ct.operation_date) < CAST(:operationDateTo AS DATE))",
           nativeQuery = true)
    Page<ClosedTransaction> findBy(
            @Param("status") String status,
            @Param("companyCompanyId") Long companyCompanyId,
            @Param("operationDateFrom") String operationDateFrom,
            @Param("operationDateTo") String operationDateTo,
            Pageable pageable);
}

