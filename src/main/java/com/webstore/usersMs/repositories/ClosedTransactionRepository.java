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
           "AND (:companyCompanyId IS NULL OR ct.company_company_id = :companyCompanyId)",
           countQuery = "SELECT COUNT(*) FROM closed_transaction ct " +
           "WHERE (:status IS NULL OR :status = '' OR ct.status ILIKE '%' || :status || '%') " +
           "AND (:companyCompanyId IS NULL OR ct.company_company_id = :companyCompanyId)",
           nativeQuery = true)
    Page<ClosedTransaction> findBy(
            @Param("status") String status,
            @Param("companyCompanyId") Long companyCompanyId,
            Pageable pageable);
}

