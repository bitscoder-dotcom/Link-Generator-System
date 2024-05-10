package com.bitscoderdotcom.link_generator_system.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "company")
@AllArgsConstructor
@Getter
@Setter
public class Company {

    @Id
    private String id;
    private String userName;
    private String Password;
    private String companyEmail;
    private String companyName;
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Invoice> invoices;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "totp_id", referencedColumnName = "username")
    private UserTOTP userTOTP;

    public Company() {
        this.setId(generateCustomUUID());
    }

    private String generateCustomUUID() {
        return "comp"+ UUID.randomUUID().toString().substring(0, 5);
    }
}
