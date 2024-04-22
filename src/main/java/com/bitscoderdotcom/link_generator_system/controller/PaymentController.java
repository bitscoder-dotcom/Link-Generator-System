package com.bitscoderdotcom.link_generator_system.controller;

import com.bitscoderdotcom.link_generator_system.service.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class PaymentController {

    private final InvoiceService invoiceService;

    @PostMapping("/payment")
    public String processPayment(@RequestParam String linkId, @RequestParam String invoiceId, Model model) {
        String message = invoiceService.processPayment(linkId, invoiceId);

        model.addAttribute("message", message);

        return "paymentSuccess";
    }
}
