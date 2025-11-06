package com.practice.Subscription.service;

import com.practice.Subscription.events.SubscriptionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "subscription-events";

    public void sendSubscriptionEvent(SubscriptionEvent event) {
        log.info("📤 Sending event to Kafka topic {}: {}", TOPIC, event);
        kafkaTemplate.send(TOPIC, event);
    }
}
