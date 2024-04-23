package com.bitscoderdotcom.link_generator_system.entities;

import com.bitscoderdotcom.link_generator_system.constant.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "invoice")
@AllArgsConstructor
@Getter
@Setter
public class Invoice {

    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    private String customerName;
    private String customerEmail;
    private String description;
    private long quantity;
    private double unitPrice;
    private double totalAmount;
    private LocalDate invoiceGenerationDate;
    private LocalDate paymentDueDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL)
    private PaymentLink paymentLink;
    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL)
    private Receipt receipt;

    public Invoice() {
        this.setId(generateCustomUUID());
    }

    private String generateCustomUUID() {
        return "invoice"+ UUID.randomUUID().toString().substring(0, 5);
    }
}


