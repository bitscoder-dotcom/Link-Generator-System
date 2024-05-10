package com.bitscoderdotcom.link_generator_system.security.service;

import com.bitscoderdotcom.link_generator_system.dto.*;
import com.bitscoderdotcom.link_generator_system.entities.Company;
import com.bitscoderdotcom.link_generator_system.entities.UserTOTP;
import com.bitscoderdotcom.link_generator_system.entities.Validation;
import com.bitscoderdotcom.link_generator_system.repository.CompanyRepository;
import com.bitscoderdotcom.link_generator_system.repository.UserTOTPRepository;
import com.bitscoderdotcom.link_generator_system.security.jwt.JwtUtils;
import com.bitscoderdotcom.link_generator_system.service.EmailService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private AuthenticationManager authenticationManager;
    private CompanyRepository companyRepository;
    private UserTOTPRepository userTOTPRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;
    private UserDetailsServiceImpl userDetailsService;
    private EmailService emailService;
    private final GoogleAuthenticator gAuth;

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


        log.info("Company registered successfully with username: {}", username);

        GoogleAuthenticatorKey key = generate2faKey(username);

        UserTOTP userTOTP = new UserTOTP();
        userTOTP.setUsername(username);
        userTOTP.setSecretKey(key.getKey());
        userTOTPRepository.save(userTOTP);

        Company company = new Company();
        company.setUserName(request.getName());
        company.setCompanyName(request.getCompanyName());
        company.setCompanyEmail(request.getEmail());
        company.setPassword(passwordEncoder.encode(request.getPassword()));
        company.setUserTOTP(userTOTP);
        companyRepository.save(company);

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(company.getCompanyEmail());
        emailDetails.setSubject("Account Registration Confirmation");
        emailDetails.setMessageBody("Your account has been registered on our platform.\n" +
                "Please scan the following QR code with your Google Authenticator app to enable 2FA.\n" +
                "Key: " + key.getKey());
        emailDetails.setAttachment(generateQRCode(username, key));
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

            return createSuccessResponse("2FA required", response);
        } catch (BadCredentialsException e) {
            log.info("Invalid email or password for email: {}", request.getEmail());
            return createBadRequestResponse("Invalid email or password", null);
        }
    }

//    @SneakyThrows
//    public File generate2fa(String username) {
//        final GoogleAuthenticatorKey key = gAuth.createCredentials(username);
//
//        UserTOTP userTOTP = new UserTOTP();
//        userTOTP.setUsername(username);
//        userTOTP.setSecretKey(key.getKey());
//        userTOTPRepository.save(userTOTP);
//
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("Link Generation App", username, key);
//        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);
//
//        File qrFile = File.createTempFile("qrcode", ".png");
//        FileOutputStream pngOutputStream = new FileOutputStream(qrFile);
//        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
//        pngOutputStream.close();
//
//        return qrFile;
//    }
    @SneakyThrows
    public GoogleAuthenticatorKey generate2faKey(String username) {
        return gAuth.createCredentials(username);
    }

    @SneakyThrows
    public File generateQRCode(String username, GoogleAuthenticatorKey key) {
        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("Link Generation App", username, key);
        BitMatrix bitMatrix = new QRCodeWriter().encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);

        File qrFile = File.createTempFile("qrcode", ".png");
        try (FileOutputStream pngOutputStream = new FileOutputStream(qrFile)) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        }

        return qrFile;
    }

    public ResponseEntity<ApiResponse<SignInRequest.Response>> validate2FA(ValidateCodeDto body) {
        log.info("validate2FA method called with username: {}", body.getUsername());

        UserTOTP userTOTP = userTOTPRepository.findByUsername(body.getUsername());

        if (userTOTP == null || !gAuth.authorizeUser(body.getUsername(), body.getVerificationCode())) {
            log.info("Invalid 2FA code for username: {}", body.getUsername());
            return createBadRequestResponse("Invalid 2FA code", null);
        }

        return createSuccessResponse("User authenticated successfully", null);
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

