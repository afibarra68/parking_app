package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByCountryId(Long countryId);

    @Query("SELECT c FROM Country c " +
            "WHERE (:countryId IS NULL OR :countryId = c.countryId) " +
            "AND (:description IS NULL OR c.description like :description )")
    List<Country> findBy(@Param("countryId") Long countryId, @Param("description") String description);

    @Query(value = "SELECT c FROM Country c " +
            "WHERE (:countryId IS NULL OR :countryId = c.countryId) " +
            "AND (:description IS NULL OR :description = '' OR c.description LIKE '%' || :description || '%') " +
            "AND (:name IS NULL OR :name = '' OR c.name LIKE '%' || :name || '%')",
            countQuery = "SELECT COUNT(c) FROM Country c " +
            "WHERE (:countryId IS NULL OR :countryId = c.countryId) " +
            "AND (:description IS NULL OR :description = '' OR c.description LIKE '%' || :description || '%') " +
            "AND (:name IS NULL OR :name = '' OR c.name LIKE '%' || :name || '%')")
    Page<Country> findByPageable(
            @Param("countryId") Long countryId,
            @Param("description") String description,
            @Param("name") String name,
            Pageable pageable);

    @Query("SELECT c FROM Country c " +
            "WHERE (:countryId IS NULL OR :countryId = c.countryId) " +
            "AND (:description IS NULL OR :description = '' OR c.description LIKE '%' || :description || '%') " +
            "AND (:name IS NULL OR :name = '' OR c.name LIKE '%' || :name || '%')")
    List<Country> findByQueryable(
            @Param("countryId") Long countryId,
            @Param("description") String description,
            @Param("name") String name);
}
