package com.bitscoderdotcom.link_generator_system.controller;

import com.bitscoderdotcom.link_generator_system.dto.ApiResponse;
import com.bitscoderdotcom.link_generator_system.dto.SignInRequest;
import com.bitscoderdotcom.link_generator_system.dto.UserRegistrationRequest;
import com.bitscoderdotcom.link_generator_system.security.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@AllArgsConstructor
@RequestMapping("/lgsApp/v1/auth")
public class AuthController {

    private AuthService authService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistrationRequest", new UserRegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(UserRegistrationRequest request, RedirectAttributes redirectAttributes) {
        String response = authService.register(request);
        if (response.equals("Company registered successfully")) {
            redirectAttributes.addFlashAttribute("message", response);
            return "redirect:/lgsApp/v1/auth/signIn";
        } else {
            redirectAttributes.addFlashAttribute("message", response);
            return "redirect:/registrationFailure";
        }
    }

    @GetMapping("/signIn")
    public String showSignInForm(Model model) {
        model.addAttribute("signInRequest", new SignInRequest());
        return "signIn";
    }

    @PostMapping("/signIn")
    public String signIn(@ModelAttribute SignInRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        ResponseEntity<ApiResponse<SignInRequest.Response>> apiResponse = authService.signIn(request);
        if (apiResponse.getStatusCode() == HttpStatus.OK) {
            // Create a new cookie
            Cookie cookie = new Cookie("token", Objects.requireNonNull(apiResponse.getBody()).getData().getToken());
            cookie.setHttpOnly(true);
            // Add the cookie to the response
            response.addCookie(cookie);
            // Redirect to the generateInvoice page
            return "redirect:/lgsApp/v1/invoice/generateInvoice";
        } else {
            redirectAttributes.addFlashAttribute("error", Objects.requireNonNull(apiResponse.getBody()).getMessage());
            return "redirect:/error";
        }
    }
}
