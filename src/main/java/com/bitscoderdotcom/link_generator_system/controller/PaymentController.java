package com.bitscoderdotcom.link_generator_system.controller;

import com.bitscoderdotcom.link_generator_system.exception.ResourceNotFoundException;
import com.bitscoderdotcom.link_generator_system.service.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class PaymentController {

    private final InvoiceService invoiceService;

    @PostMapping("/payment")
    public String processPayment(@RequestParam String linkId, @RequestParam String invoiceId, RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<String> responseEntity = invoiceService.processPayment(linkId, invoiceId);
            String message = responseEntity.getBody();

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                redirectAttributes.addFlashAttribute("message", message);
                return "/paymentSuccess";
            } else {
                redirectAttributes.addAttribute("error", message);
                return "/error";
            }
        } catch (Exception ex) {
            throw new RuntimeException("An error occurred while processing the payment.");
        }
    }
}
