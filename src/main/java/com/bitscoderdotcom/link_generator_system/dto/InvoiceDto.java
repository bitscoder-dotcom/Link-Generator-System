package com.bitscoderdotcom.link_generator_system.dto;

import com.bitscoderdotcom.link_generator_system.constant.Status;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {

    private String customerName;
    private String customerEmail;
    private String description;
    private long quantity;
    private double unitPrice;
    private double totalAmount;
    private LocalDate invoiceGenerationDate;
    private LocalDate paymentDueDate;
    private Status status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private String id;
        private String customerName;
        private String customerEmail;
        private String description;
        private long quantity;
        private double unitPrice;
        private double totalAmount;
        private LocalDate invoiceGenerationDate;
        private LocalDate paymentDueDate;
        private Status status;
    }
}