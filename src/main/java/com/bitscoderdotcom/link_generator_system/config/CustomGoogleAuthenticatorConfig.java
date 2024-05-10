package com.bitscoderdotcom.link_generator_system.config;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.ICredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CustomGoogleAuthenticatorConfig {

    private final ICredentialRepository credentialRepository;

    @Bean
    public GoogleAuthenticator gAuth() {

        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

        googleAuthenticator.setCredentialRepository(credentialRepository);

        return googleAuthenticator;

    }
}
