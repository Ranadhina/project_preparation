package com.practice.Subscription.repo;

import com.practice.Subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByCustomerIdAndStatus(String customerId, String status);
}

