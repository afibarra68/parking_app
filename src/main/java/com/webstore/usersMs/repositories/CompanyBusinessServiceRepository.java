package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.CompanyBusinessService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyBusinessServiceRepository extends JpaRepository<CompanyBusinessService, Long> {

    Optional<CompanyBusinessService> findByCompanyBusinessServiceId(Long companyBusinessServiceId);

    @Query("SELECT cbs FROM CompanyBusinessService cbs " +
            "WHERE (:companyBusinessServiceId IS NULL OR :companyBusinessServiceId = cbs.companyBusinessServiceId) " +
            "AND (:companyId IS NULL OR :companyId = cbs.company.companyId) " +
            "AND (:businessServiceId IS NULL OR :businessServiceId = cbs.businessService.businessServiceId)")
    List<CompanyBusinessService> findBy(
            @Param("companyBusinessServiceId") Long companyBusinessServiceId,
            @Param("companyId") Long companyId,
            @Param("businessServiceId") Long businessServiceId);

    @Query("SELECT DISTINCT cbs FROM CompanyBusinessService cbs " +
           "LEFT JOIN FETCH cbs.businessService " +
           "LEFT JOIN FETCH cbs.company " +
           "WHERE cbs.company.companyId = :companyId")
    List<CompanyBusinessService> findByCompanyId(@Param("companyId") Long companyId);

    List<CompanyBusinessService> findByBusinessService_BusinessServiceId(Long businessServiceId);

}

