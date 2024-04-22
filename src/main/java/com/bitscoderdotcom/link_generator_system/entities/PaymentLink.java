package com.bitscoderdotcom.link_generator_system.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "link")
@AllArgsConstructor
@Getter
@Setter
public class PaymentLink {

    @Id
    private String id;
    @OneToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
    private String url;

    public PaymentLink() {
        this.setId(generateCustomUUID());
    }

    private String generateCustomUUID() {
        return "url"+ UUID.randomUUID().toString().substring(0, 5);
    }
}


