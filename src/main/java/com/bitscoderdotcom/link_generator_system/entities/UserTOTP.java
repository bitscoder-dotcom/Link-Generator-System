package com.bitscoderdotcom.link_generator_system.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "user_totp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTOTP {

    @Id
    private String username;

    private String secretKey;

    private int validationCode;

    private List<Integer> scratchCodes;
}
