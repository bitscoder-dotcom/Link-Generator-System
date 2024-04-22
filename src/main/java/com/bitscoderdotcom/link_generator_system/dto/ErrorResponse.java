package com.bitscoderdotcom.link_generator_system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ErrorResponse {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestTime;
    private boolean status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(LocalDateTime requestTime, boolean status, String error, String message, String path) {
        this.requestTime = requestTime;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
