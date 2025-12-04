package com.webstore.usersMs.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webstore.usersMs.entities.ClosedTransaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClosedTransactionRepository extends JpaRepository<ClosedTransaction, Long> {

    Optional<ClosedTransaction> findByClosedTransactionId(Long closedTransactionId);

    @Query("SELECT ct FROM ClosedTransaction ct " +
           "LEFT JOIN FETCH ct.company c " +
           "LEFT JOIN FETCH c.country " +
           "WHERE (:status IS NULL OR :status = '' OR ct.status LIKE '%' || :status || '%') " +
           "AND (:companyCompanyId IS NULL OR ct.company.companyId = :companyCompanyId)")
    Page<ClosedTransaction> findBy(
            @Param("status") String status,
            @Param("companyCompanyId") Long companyCompanyId,
            Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM closed_transaction ct " +
           "WHERE ct.company_company_id = :companyId " +
           "AND ct.status = 'CLOSED' " +
           "AND ct.operation_date >= CAST(CURRENT_DATE AS TIMESTAMP) " +
           "AND ct.operation_date < CAST(CURRENT_DATE + INTERVAL '1 day' AS TIMESTAMP)",
           nativeQuery = true)
    Long countTodayTransactions(@Param("companyId") Long companyId);

    @Query(value = "SELECT COALESCE(SUM(ct.total_amount), 0.0) FROM closed_transaction ct " +
           "WHERE ct.company_company_id = :companyId " +
           "AND ct.status = 'CLOSED' " +
           "AND ct.operation_date >= CAST(CURRENT_DATE AS TIMESTAMP) " +
           "AND ct.operation_date < CAST(CURRENT_DATE + INTERVAL '1 day' AS TIMESTAMP)",
           nativeQuery = true)
    Double sumTodayAmount(@Param("companyId") Long companyId);

    @Query("SELECT ct FROM ClosedTransaction ct " +
           "LEFT JOIN FETCH ct.company c " +
           "LEFT JOIN FETCH c.country " +
           "WHERE ct.company.companyId = :companyId " +
           "AND ct.status = 'CLOSED' " +
           "AND ct.operationDate >= :startOfDay " +
           "AND ct.operationDate < :startOfNextDay " +
           "ORDER BY ct.operationDate DESC")
    List<ClosedTransaction> findTodayTransactions(
            @Param("companyId") Long companyId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("startOfNextDay") LocalDateTime startOfNextDay);
}

