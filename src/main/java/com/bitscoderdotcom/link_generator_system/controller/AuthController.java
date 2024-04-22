package com.bitscoderdotcom.link_generator_system.controller;

import com.bitscoderdotcom.link_generator_system.dto.ApiResponse;
import com.bitscoderdotcom.link_generator_system.dto.SignInRequest;
import com.bitscoderdotcom.link_generator_system.dto.UserRegistrationRequest;
import com.bitscoderdotcom.link_generator_system.security.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/lgsApp/v1/auth")
public class AuthController {

    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserRegistrationRequest.Response>> register(@RequestBody UserRegistrationRequest request) {
        return authService.register(request);
    }

    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse<SignInRequest.Response>> signIn(@RequestBody SignInRequest request) {
        return authService.signIn(request);
    }
}
