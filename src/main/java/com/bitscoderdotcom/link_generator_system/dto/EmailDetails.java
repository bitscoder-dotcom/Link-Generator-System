package com.bitscoderdotcom.link_generator_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EmailDetails {

    private String recipient;
    private String subject;
    private String messageBody;
    private File attachment;
}
