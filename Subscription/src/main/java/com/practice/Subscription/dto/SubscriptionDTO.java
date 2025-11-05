package com.practice.Subscription.dto;

import com.practice.Subscription.enums.PlanType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object representing a Subscription")
public class SubscriptionDTO {

    @Schema(description = "Unique identifier for the subscription", example = "101")
    private Long id;

    @Schema(description = "ID of the customer associated with this subscription", example = "1")
    private Long customerId;

    @Schema(description = "Type of plan chosen by the customer", example = "MONTHLY", allowableValues = {"MONTHLY", "QUARTERLY", "ANNUAL"})
    private PlanType planType;

    @Schema(description = "Subscription start date", example = "2025-11-01")
    private LocalDate startDate;

    @Schema(description = "Subscription end date", example = "2025-11-30")
    private LocalDate endDate;

    @Schema(description = "Current status of the subscription", example = "ACTIVE", allowableValues = {"ACTIVE", "EXPIRED", "CANCELLED"})
    private String status;

    @Schema(description = "Price charged for the selected plan", example = "499.0")
    private Double price;
}
