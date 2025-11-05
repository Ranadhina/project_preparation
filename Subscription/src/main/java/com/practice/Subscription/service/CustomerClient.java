package com.practice.Subscription.service;

import com.practice.Subscription.dto.CustomerResponseDTO;
import com.practice.Subscription.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
public class CustomerClient {

    private final WebClient webClient;

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    public CustomerClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public CustomerResponseDTO getCustomerById(Long customerId) {
        try {
            log.info("Fetching customer details for ID: {}", customerId);

            return webClient.get()
                    .uri(customerServiceUrl + "/customers/" + customerId)
                    .retrieve()
                    .bodyToMono(CustomerResponseDTO.class)
                    .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                        log.error("Customer not found: {}", e.getMessage());
                        throw new ResourceNotFoundException("Customer not found with id: " + customerId);
                    })
                    .block(); // blocking since we're in a sync service layer

        } catch (WebClientResponseException ex) {
            log.error("Error fetching customer {}: {}", customerId, ex.getResponseBodyAsString());
            throw new ResourceNotFoundException("Unable to fetch customer: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error while calling customer service", ex);
            throw new RuntimeException("Customer service unavailable");
        }
    }
}
