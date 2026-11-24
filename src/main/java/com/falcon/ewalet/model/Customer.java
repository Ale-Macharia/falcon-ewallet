package com.falcon.ewalet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true, length = 15)
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nationalId;

    @Column(nullable = false, length = 100)
    private String pin;

    @Column(nullable = false)
    private String role = "CUSTOMER";

    private LocalDateTime createdAt = LocalDateTime.now();
}