package com.webstore.usersMs.repositories;

import com.webstore.usersMs.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
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
}
