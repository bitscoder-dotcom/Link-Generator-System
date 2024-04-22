package com.bitscoderdotcom.link_generator_system.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "receipt")
@AllArgsConstructor
@Getter
@Setter
public class Receipt {

    @Id
    private String id;
    @OneToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
    private Date paymentDue;
    private double amountPaid;

    public Receipt() {
        this.setId(generateCustomUUID());
    }

    private String generateCustomUUID() {
        return "receipt"+ UUID.randomUUID().toString().substring(0, 5);
    }
}

