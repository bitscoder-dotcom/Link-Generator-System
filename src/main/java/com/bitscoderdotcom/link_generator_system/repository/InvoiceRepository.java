package com.bitscoderdotcom.link_generator_system.repository;

import com.bitscoderdotcom.link_generator_system.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
}
