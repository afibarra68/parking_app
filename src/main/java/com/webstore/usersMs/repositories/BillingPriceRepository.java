package com.webstore.usersMs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webstore.usersMs.entities.BillingPrice;

import java.util.Optional;

@Repository
public interface BillingPriceRepository extends JpaRepository<BillingPrice, Long> {

    Optional<BillingPrice> findByBillingPriceId(Long billingPriceId);
}

