package com.bitscoderdotcom.link_generator_system.repository;

import com.bitscoderdotcom.link_generator_system.entities.PaymentLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLinkRepository extends JpaRepository<PaymentLink, String> {
}
