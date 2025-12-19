package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.TicketTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketTemplateRepository extends JpaRepository<TicketTemplate, Long> {

    Optional<TicketTemplate> findByTicketTemplateId(Long ticketTemplateId);

    @Query("SELECT tt FROM TicketTemplate tt " +
            "WHERE (:ticketTemplateId IS NULL OR :ticketTemplateId = tt.ticketTemplateId) " +
            "AND (:printerType IS NULL OR :printerType = '' OR tt.printerType = :printerType) " +
            "AND (:ticketType IS NULL OR :ticketType = '' OR tt.ticketType = :ticketType) " +
            "AND (:companyCompanyId IS NULL OR tt.company.companyId = :companyCompanyId) " +
            "AND (:userUserId IS NULL OR tt.user.appUserId = :userUserId)")
    List<TicketTemplate> findBy(
            @Param("ticketTemplateId") Long ticketTemplateId,
            @Param("printerType") String printerType,
            @Param("ticketType") String ticketType,
            @Param("companyCompanyId") Long companyCompanyId,
            @Param("userUserId") Long userUserId);

    @Query(value = "SELECT tt FROM TicketTemplate tt " +
            "WHERE (:ticketTemplateId IS NULL OR :ticketTemplateId = tt.ticketTemplateId) " +
            "AND (:printerType IS NULL OR :printerType = '' OR tt.printerType = :printerType) " +
            "AND (:ticketType IS NULL OR :ticketType = '' OR tt.ticketType = :ticketType) " +
            "AND (:companyCompanyId IS NULL OR tt.company.companyId = :companyCompanyId) " +
            "AND (:userUserId IS NULL OR tt.user.appUserId = :userUserId)",
            countQuery = "SELECT COUNT(tt) FROM TicketTemplate tt " +
            "WHERE (:ticketTemplateId IS NULL OR :ticketTemplateId = tt.ticketTemplateId) " +
            "AND (:printerType IS NULL OR :printerType = '' OR tt.printerType = :printerType) " +
            "AND (:ticketType IS NULL OR :ticketType = '' OR tt.ticketType = :ticketType) " +
            "AND (:companyCompanyId IS NULL OR tt.company.companyId = :companyCompanyId) " +
            "AND (:userUserId IS NULL OR tt.user.appUserId = :userUserId)")
    Page<TicketTemplate> findByPageable(
            @Param("ticketTemplateId") Long ticketTemplateId,
            @Param("printerType") String printerType,
            @Param("ticketType") String ticketType,
            @Param("companyCompanyId") Long companyCompanyId,
            @Param("userUserId") Long userUserId,
            Pageable pageable);

}

