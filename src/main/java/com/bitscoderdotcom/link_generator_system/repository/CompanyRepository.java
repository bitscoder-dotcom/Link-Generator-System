package com.bitscoderdotcom.link_generator_system.repository;

import com.bitscoderdotcom.link_generator_system.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CompanyRepository extends JpaRepository<Company, String> {
    Optional<Company> findCompanyByCompanyEmail(String email);

    boolean existsByCompanyEmail(String email);

    boolean existsByUserName(String username);

    Optional<Company> findUserByCompanyEmail(String email);
}
