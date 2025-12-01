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

    Optional<BillingPrice> findByBillingPriceId(Long billingPriceId);

    @Query("SELECT bp FROM BillingPrice bp " +
            "WHERE (:status IS NULL OR :status = '' OR bp.status LIKE '%' || :status || '%') " +
            "AND (:companyCompanyId IS NULL OR bp.company.companyId = :companyCompanyId) " +
            "AND (:coverType IS NULL OR :coverType = '' OR bp.coverType LIKE '%' || :coverType || '%')")
    List<BillingPrice> findBy(
            @Param("status") String status,
            @Param("companyCompanyId") Long companyCompanyId,
            @Param("coverType") String coverType);

    @Query(value = "SELECT bp FROM BillingPrice bp " +
            "WHERE (:status IS NULL OR :status = '' OR bp.status LIKE '%' || :status || '%') " +
            "AND (:companyCompanyId IS NULL OR bp.company.companyId = :companyCompanyId) " +
            "AND (:coverType IS NULL OR :coverType = '' OR bp.coverType LIKE '%' || :coverType || '%')",
            countQuery = "SELECT COUNT(bp) FROM BillingPrice bp " +
            "WHERE (:status IS NULL OR :status = '' OR bp.status LIKE '%' || :status || '%') " +
            "AND (:companyCompanyId IS NULL OR bp.company.companyId = :companyCompanyId) " +
            "AND (:coverType IS NULL OR :coverType = '' OR bp.coverType LIKE '%' || :coverType || '%')")
    Page<BillingPrice> findByPageable(
            @Param("status") String status,
            @Param("companyCompanyId") Long companyCompanyId,
            @Param("coverType") String coverType,
            Pageable pageable);
}

