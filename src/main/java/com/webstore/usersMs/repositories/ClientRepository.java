package com.webstore.usersMs.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webstore.usersMs.entities.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByClientId(Long clientId);

    @Query(value = "SELECT c.* FROM client c " +
           "WHERE (:document IS NULL OR :document = '' OR c.number_identity ILIKE '%' || :document || '%')",
           countQuery = "SELECT COUNT(*) FROM client c " +
           "WHERE (:document IS NULL OR :document = '' OR c.number_identity ILIKE '%' || :document || '%')",
           nativeQuery = true)
    Page<Client> findBy(@Param("document") String document, Pageable pageable);
}
