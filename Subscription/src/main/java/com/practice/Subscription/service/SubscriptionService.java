package com.practice.Subscription.service;

import com.practice.Subscription.dto.SubscriptionDTO;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO);

    SubscriptionDTO getSubscriptionById(Long id);

    List<SubscriptionDTO> getAllSubscriptions();

    SubscriptionDTO updateSubscription(Long id, SubscriptionDTO subscriptionDTO);

    void deleteSubscription(Long id);
}
