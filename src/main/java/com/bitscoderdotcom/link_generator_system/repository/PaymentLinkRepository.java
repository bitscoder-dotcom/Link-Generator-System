package com.bitscoderdotcom.link_generator_system.repository;

import com.bitscoderdotcom.link_generator_system.entities.PaymentLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLinkRepository extends JpaRepository<PaymentLink, String> {
}
