package com.practice.Subscription.model;

import com.practice.Subscription.convertor.EncryptionConverter;
import com.practice.Subscription.enums.PlanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔐 Encrypt customerId
    @Convert(converter = EncryptionConverter.class)
    private String customerId;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status;

    // 🔐 Encrypt price
    @Convert(converter = EncryptionConverter.class)
    private String price;
}
