package com.bitscoderdotcom.link_generator_system.repository;

import com.bitscoderdotcom.link_generator_system.entities.UserTOTP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTOTPRepository extends JpaRepository<UserTOTP, String> {

    UserTOTP findByUsername(String username);
}
