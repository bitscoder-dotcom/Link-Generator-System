package com.bitscoderdotcom.link_generator_system.security.service;

import com.bitscoderdotcom.link_generator_system.entities.Company;
import com.bitscoderdotcom.link_generator_system.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Trying to get user by email");
        Company company = companyRepository.findCompanyByCompanyEmail(email)
                .orElseThrow(() -> {
                    log.error("Company not found with email: {}", email);
                    return new UsernameNotFoundException("Company not found with email: " + email);
                });

        log.info("Found COMPANY user: {}", email);
        return buildUserDetails(company);
    }

    private UserDetails buildUserDetails(Company company) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("COMPANY"));

        log.info("Building UserDetails for user: {}", company.getUserName());
        return new UserDetailsImpl(
                company.getId(),
                company.getUserName(),
                company.getCompanyEmail(),
                company.getPassword(),
                authorities
        );
    }
}
