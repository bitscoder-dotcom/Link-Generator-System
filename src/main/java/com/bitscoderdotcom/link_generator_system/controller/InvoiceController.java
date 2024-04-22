package com.bitscoderdotcom.link_generator_system.controller;

import com.bitscoderdotcom.link_generator_system.dto.ApiResponse;
import com.bitscoderdotcom.link_generator_system.dto.InvoiceDto;
import com.bitscoderdotcom.link_generator_system.entities.Invoice;
import com.bitscoderdotcom.link_generator_system.service.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@AllArgsConstructor
@RequestMapping("/lgsApp/v1/invoice")
public class InvoiceController {

    private InvoiceService invoiceService;

    @PostMapping("/generateInvoice")
    public ResponseEntity<ApiResponse<InvoiceDto.Response>> generateInvoice(@RequestBody InvoiceDto invoiceDto, Principal principal) {
        return invoiceService.generateInvoice(invoiceDto, principal);
    }

    @GetMapping("/{invoiceId}/payment")
    public String showPaymentPage(@PathVariable String invoiceId, Model model) {

        Invoice invoice = invoiceService.getInvoiceById(invoiceId);

        model.addAttribute("invoice", invoice);

        return "payment";
    }
}
