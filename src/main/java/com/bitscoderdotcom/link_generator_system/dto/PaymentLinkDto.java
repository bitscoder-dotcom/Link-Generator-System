package com.bitscoderdotcom.link_generator_system.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkDto {

    private String url;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private String id;
        private String url;
    }
}
