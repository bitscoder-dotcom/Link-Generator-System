package com.bitscoderdotcom.link_generator_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SignInRequest {

    @NotBlank(message = "email should not be blank")
    private String email;
    @NotBlank(message = "password should not be blank")
    private String password;
    @NotBlank(message = "authCode should not be blank")
    private String authCode;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private String userId;
        private String token;
        private String type = "Bearer";
        private String name;
        private LocalDateTime tokenExpirationDate;

        public Response(String userId, String token, String name, LocalDateTime tokenExpirationDate) {
            this.userId = userId;
            this.token = token;
            this.name = name;
            this.tokenExpirationDate = tokenExpirationDate;
        }
    }
}
