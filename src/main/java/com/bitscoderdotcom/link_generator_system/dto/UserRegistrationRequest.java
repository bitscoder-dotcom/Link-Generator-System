package com.bitscoderdotcom.link_generator_system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRegistrationRequest {

    private String name;
    private String companyName;
    @Email(message = "Not a valid email")
    private String email;
    private String password;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class Response<T> {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime requestTime = LocalDateTime.now();
        private String referenceId = UUID.randomUUID().toString();
        private boolean status;
        private String message;
        private T data;

        public Response(LocalDateTime requestTime, String referenceId,
                        boolean status, String message, T data) {
            this.requestTime = requestTime;
            this.referenceId = referenceId;
            this.status = status;
            this.message = message;
            this.data = data;
        }
    }
}
