package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.BusinessService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessServiceRepository extends JpaRepository<BusinessService, Long> {

    Optional<BusinessService> findByBusinessServiceId(Long businessServiceId);

    @Query("SELECT bs FROM BusinessService bs " +
            "WHERE (:businessServiceId IS NULL OR :businessServiceId = bs.businessServiceId) " +
            "AND (:principalName IS NULL OR :principalName = '' OR bs.principalName LIKE '%' || :principalName || '%') " +
            "AND (:code IS NULL OR :code = '' OR bs.code LIKE '%' || :code || '%') " +
            "AND (:description IS NULL OR :description = '' OR bs.description LIKE '%' || :description || '%')")
    List<BusinessService> findBy(
            @Param("businessServiceId") Long businessServiceId,
            @Param("principalName") String principalName,
            @Param("code") String code,
            @Param("description") String description);

    @Query(value = "SELECT bs FROM BusinessService bs " +
            "WHERE (:businessServiceId IS NULL OR :businessServiceId = bs.businessServiceId) " +
            "AND (:principalName IS NULL OR :principalName = '' OR bs.principalName LIKE '%' || :principalName || '%') " +
            "AND (:code IS NULL OR :code = '' OR bs.code LIKE '%' || :code || '%') " +
            "AND (:description IS NULL OR :description = '' OR bs.description LIKE '%' || :description || '%')",
            countQuery = "SELECT COUNT(bs) FROM BusinessService bs " +
            "WHERE (:businessServiceId IS NULL OR :businessServiceId = bs.businessServiceId) " +
            "AND (:principalName IS NULL OR :principalName = '' OR bs.principalName LIKE '%' || :principalName || '%') " +
            "AND (:code IS NULL OR :code = '' OR bs.code LIKE '%' || :code || '%') " +
            "AND (:description IS NULL OR :description = '' OR bs.description LIKE '%' || :description || '%')")
    Page<BusinessService> findByPageable(
            @Param("businessServiceId") Long businessServiceId,
            @Param("principalName") String principalName,
            @Param("code") String code,
            @Param("description") String description,
            Pageable pageable);

}

