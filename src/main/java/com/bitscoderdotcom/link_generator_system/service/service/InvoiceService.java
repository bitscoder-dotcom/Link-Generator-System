package com.bitscoderdotcom.link_generator_system.service.service;

import com.bitscoderdotcom.link_generator_system.dto.ApiResponse;
import com.bitscoderdotcom.link_generator_system.dto.InvoiceDto;
import com.bitscoderdotcom.link_generator_system.entities.Invoice;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface InvoiceService {

    ResponseEntity<ApiResponse<InvoiceDto.Response>> generateInvoice(InvoiceDto invoiceDto, Principal principal);
    String processPayment(String linkId, String invoiceId);
    Invoice getInvoiceById(String id);
}
