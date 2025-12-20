package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.TicketTemplate;
import com.webstore.usersMs.entities.enums.EPrinterType;
import com.webstore.usersMs.entities.enums.ETicketType;
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

    @Query("SELECT tt FROM TicketTemplate tt join Printer p on p. " +
            "WHERE (tt.printerType = :printerType) " +
            "AND ( tt.ticketType = :ticketType) " +
            "AND (tt.company.companyId = tt.printer.companyId) " +
            "AND (tt.printer.user = tt.user) " +
            "AND (tt.user.appUserId = :userUserId)" +
            "AND (tt.printer.isActive = :active)" +
            "AND (tt.company.companyId = :companyId)"
    )
    Optional<TicketTemplate> findByOptions(
            @Param("printerType") EPrinterType printerType,
            @Param("ticketType") ETicketType ticketType,
            @Param("userUserId") Long userUserId,
            @Param("active") boolean active,
            @Param("companyId") Long companyCompanyId
            );

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

