package com.bitscoderdotcom.link_generator_system.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

    private String userName;
    private String Password;
    private String companyEmail;
    private String companyName;
    private List<Long> invoiceIds;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private String id;
        private String userName;
        private String Password;
        private String companyEmail;
        private String companyName;
        private List<String> invoiceIds;
    }
}