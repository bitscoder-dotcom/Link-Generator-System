package com.bitscoderdotcom.link_generator_system.security.service;

import com.bitscoderdotcom.link_generator_system.dto.ApiResponse;
import com.bitscoderdotcom.link_generator_system.dto.EmailDetails;
import com.bitscoderdotcom.link_generator_system.dto.SignInRequest;
import com.bitscoderdotcom.link_generator_system.dto.UserRegistrationRequest;
import com.bitscoderdotcom.link_generator_system.entities.Company;
import com.bitscoderdotcom.link_generator_system.repository.CompanyRepository;
import com.bitscoderdotcom.link_generator_system.security.jwt.JwtUtils;
import com.bitscoderdotcom.link_generator_system.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private AuthenticationManager authenticationManager;
    private CompanyRepository companyRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;
    private UserDetailsServiceImpl userDetailsService;
    private EmailService emailService;

    @Transactional
    public String register(UserRegistrationRequest request) {

        log.info("Register method called");

        String username = request.getName();
        String email = request.getEmail();

        boolean usernameExists = companyRepository.existsByUserName(username);
        boolean emailExists = companyRepository.existsByCompanyEmail(email);

        if (usernameExists) {
            log.info("Username already taken: {}", username);
            return "Username is already taken!";
        }

        if (emailExists) {
            log.info("Email already in use: {}", email);
            return "Email Address already in use!";
        }

        Company company = new Company();
        company.setUserName(request.getName());
        company.setCompanyName(request.getCompanyName());
        company.setCompanyEmail(request.getEmail());
        company.setPassword(passwordEncoder.encode(request.getPassword()));
        companyRepository.save(company);

        log.info("Company registered successfully with username: {}", username);

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(company.getCompanyEmail());
        emailDetails.setSubject("Account Registration Confirmation");
        emailDetails.setMessageBody("Your account has been registered on our platform");
        emailService.sendEmail(emailDetails);

        return "Company registered successfully";
    }

    public ResponseEntity<ApiResponse<SignInRequest.Response>> signIn(SignInRequest request) {
        log.info("SignIn method called with email: {}", request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            log.info("Company signed in successfully with email: {}", request.getEmail());

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            SignInRequest.Response response = new SignInRequest.Response(
                    userDetails.getId(),
                    jwt,
                    "Bearer",
                    userDetails.getUsername(),
                    jwtUtils.getJwtExpirationDate()
            );

            return createSuccessResponse("Company signed in successfully", response);
        } catch (BadCredentialsException e) {
            log.info("Invalid email or password for email: {}", request.getEmail());
            return createBadRequestResponse("Invalid email or password", null);
        }
    }

    public <T> ResponseEntity<ApiResponse<T>> createSuccessResponse(String message, T data) {
        return ResponseEntity.ok(new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                message,
                data
        ));
    }

    public <T> ResponseEntity<ApiResponse<T>> createBadRequestResponse(String message, T data) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponse<>(
                        LocalDateTime.now(),
                        UUID.randomUUID().toString(),
                        false,
                        message,
                        data
                )
        );
    }
}

