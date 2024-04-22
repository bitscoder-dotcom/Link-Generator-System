package com.bitscoderdotcom.link_generator_system.dto;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDto {

    private Date paymentDue;
    private double amountPaid;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private String id;
        private Date paymentDue;
        private double amountPaid;
    }
}