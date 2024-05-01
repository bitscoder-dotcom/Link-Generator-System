package com.bitscoderdotcom.link_generator_system.controller;

import com.bitscoderdotcom.link_generator_system.dto.ApiResponse;
import com.bitscoderdotcom.link_generator_system.dto.InvoiceDto;
import com.bitscoderdotcom.link_generator_system.entities.Invoice;
import com.bitscoderdotcom.link_generator_system.service.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/generateInvoice")
    public String showInvoiceForm(Model model) {
        model.addAttribute("invoiceDto", new InvoiceDto());
        return "generateInvoice";
    }

    @PostMapping("/generateInvoice")
    public String generateInvoice(@ModelAttribute InvoiceDto invoiceDto, Principal principal, Model model) {
        ResponseEntity<ApiResponse<InvoiceDto.Response>> apiResponse = invoiceService.generateInvoice(invoiceDto, principal);

        // Check if the invoice was generated successfully
        if (apiResponse != null && apiResponse.getStatusCode() == HttpStatus.OK) {
            // Add a success message
            model.addAttribute("message", "Invoice generated successfully. An email has been sent to the customer.");
        } else {
            // Add an error message
            model.addAttribute("error", "There was an error generating the invoice.");
        }

        // Stay on the same page
        return "generateInvoice";
    }

    @GetMapping("/{invoiceId}/payment")
    public String showPaymentPage(@PathVariable String invoiceId, Model model) {

        Invoice invoice = invoiceService.getInvoiceById(invoiceId);

        model.addAttribute("invoice", invoice);

        return "payment";
    }
}
