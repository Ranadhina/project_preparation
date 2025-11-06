package com.practice.Subscription.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEvent {
    private Long subscriptionId;
    private Long customerId;
    private String planName;
    private String action; // e.g., CREATED, UPDATED, DELETED
}
