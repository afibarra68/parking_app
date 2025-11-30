package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByCompanyId(Long companyId);

    @Query("SELECT c FROM Company c " +
            "WHERE (:companyId IS NULL OR :companyId = c.companyId) " +
            "AND (:companyName IS NULL OR :companyName = '' OR c.companyName LIKE '%' || :companyName || '%') " +
            "AND (:numberIdentity IS NULL OR :numberIdentity = '' OR c.numberIdentity LIKE '%' || :numberIdentity || '%')")
    List<Company> findBy(
            @Param("companyId") Long companyId,
            @Param("companyName") String companyName,
            @Param("numberIdentity") String numberIdentity);

}

