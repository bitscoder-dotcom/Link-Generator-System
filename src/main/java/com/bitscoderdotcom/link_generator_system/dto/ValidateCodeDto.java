package com.bitscoderdotcom.link_generator_system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ValidateCodeDto {

    private String username;
    private int verificationCode;
}
