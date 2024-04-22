package com.bitscoderdotcom.link_generator_system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
public class ApiResponse<T> {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestTime = LocalDateTime.now();
    private String referenceId = UUID.randomUUID().toString();
    private boolean status;
    private String message;
    private T data;

    public ApiResponse(LocalDateTime requestTime, String referenceId, boolean status, String message, T data) {
        this.requestTime = requestTime;
        this.referenceId = referenceId;
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
