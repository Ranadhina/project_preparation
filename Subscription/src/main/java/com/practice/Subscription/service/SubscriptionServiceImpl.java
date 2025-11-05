package com.practice.Subscription.service;

import com.practice.Subscription.dto.CustomerResponseDTO;
import com.practice.Subscription.enums.PlanType;
import com.practice.Subscription.dto.SubscriptionDTO;
import com.practice.Subscription.exception.ResourceNotFoundException;
import com.practice.Subscription.model.Subscription;
import com.practice.Subscription.repo.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        log.info("[CREATE] Request received for customerId={} with planType={}",
                subscriptionDTO.getCustomerId(), subscriptionDTO.getPlanType());

        Subscription subscription = mapToEntity(subscriptionDTO);

        // 🔍 Find latest subscription for this customer
        List<Subscription> existingSubs = subscriptionRepository.findAll().stream()
                .filter(sub -> sub.getCustomerId().equals(String.valueOf(subscription.getCustomerId())))
                .sorted(Comparator.comparing(Subscription::getEndDate).reversed())
                .collect(Collectors.toList());

        LocalDate startDate;
        LocalDate endDate;

        if (!existingSubs.isEmpty()) {
            Subscription latest = existingSubs.get(0);
            if (latest.getEndDate() != null && latest.getEndDate().isAfter(LocalDate.now())) {
                startDate = latest.getEndDate().plusDays(1);
                log.info("[CREATE] Customer {} already has active plan until {}. Scheduling next plan from {}",
                        subscription.getCustomerId(), latest.getEndDate(), startDate);
            } else {
                startDate = LocalDate.now();
                log.info("[CREATE] No active plan found. Starting immediately for customer {}", subscription.getCustomerId());
            }
        } else {
            startDate = LocalDate.now();
            log.info("[CREATE] First-time subscription for customer {}", subscription.getCustomerId());
        }

        PlanType planType = subscription.getPlanType();
        endDate = startDate.plusMonths(planType.getDurationInMonths());

        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setPrice(String.valueOf(planType.getPrice())); // encrypts automatically

        Subscription saved = subscriptionRepository.save(subscription);

        log.info("[CREATE] Subscription created successfully with id={} for customer={} valid till={}",
                saved.getId(), saved.getCustomerId(), saved.getEndDate());

        return mapToDTO(saved);
    }

    @Override
    public SubscriptionDTO getSubscriptionById(Long id) {
        log.debug("[GET] Fetching subscription with id={}", id);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[GET] Subscription not found with id={}", id);
                    return new ResourceNotFoundException("Subscription not found with id: " + id);
                });

        log.info("[GET] Found subscription id={} for customer={}", id, subscription.getCustomerId());
        return mapToDTO(subscription);
    }

    @Override
    public List<SubscriptionDTO> getAllSubscriptions() {
        log.info("[GET_ALL] Fetching all subscriptions");
        return subscriptionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionDTO updateSubscription(Long id, SubscriptionDTO subscriptionDTO) {
        log.info("[UPDATE] Request to update subscription id={} with planType={}", id, subscriptionDTO.getPlanType());

        Subscription existing = subscriptionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[UPDATE] Subscription not found with id={}", id);
                    return new ResourceNotFoundException("Subscription not found with id: " + id);
                });

        if (existing.getStatus().equalsIgnoreCase("ACTIVE")
                && existing.getEndDate() != null
                && existing.getEndDate().isAfter(LocalDate.now())) {
            log.warn("[UPDATE] Cannot update active subscription id={} (ends on {})", id, existing.getEndDate());
            throw new IllegalStateException(String.format(
                    "Cannot update subscription because current plan (%s) is active until %s",
                    existing.getPlanType(), existing.getEndDate()
            ));
        }

        existing.setCustomerId(String.valueOf(subscriptionDTO.getCustomerId()));
        existing.setPlanType(subscriptionDTO.getPlanType());
        existing.setStatus(subscriptionDTO.getStatus());

        PlanType planType = existing.getPlanType();
        existing.setPrice(String.valueOf(planType.getPrice()));
        existing.setStartDate(LocalDate.now());
        existing.setEndDate(existing.getStartDate().plusMonths(planType.getDurationInMonths()));

        Subscription updated = subscriptionRepository.save(existing);
        log.info("[UPDATE] Subscription id={} updated successfully for customer={}", id, updated.getCustomerId());

        return mapToDTO(updated);
    }

    @Override
    public void deleteSubscription(Long id) {
        log.info("[DELETE] Request to delete subscription id={}", id);
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[DELETE] Subscription not found with id={}", id);
                    return new ResourceNotFoundException("Subscription not found with id: " + id);
                });
        subscriptionRepository.delete(subscription);
        log.info("[DELETE] Subscription id={} deleted successfully", id);
    }

    // Convert Entity -> DTO (decrypting is automatic via JPA converter)
    private SubscriptionDTO mapToDTO(Subscription subscription) {
        Long customerId = null;
        Double price = null;

        // subscription.getCustomerId() is a String (possibly null)
        String customerIdStr = subscription.getCustomerId();
        if (customerIdStr != null && !customerIdStr.isBlank()) {
            try {
                customerId = Long.parseLong(customerIdStr);
            } catch (NumberFormatException nfe) {
                log.warn("[mapToDTO] Failed to parse customerId '{}' for subscription id={}", customerIdStr, subscription.getId(), nfe);
                throw new IllegalStateException("Stored customerId is invalid for subscription id=" + subscription.getId());
            }
        }

        String priceStr = subscription.getPrice();
        if (priceStr != null && !priceStr.isBlank()) {
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException nfe) {
                log.warn("[mapToDTO] Failed to parse price '{}' for subscription id={}", priceStr, subscription.getId(), nfe);
                throw new IllegalStateException("Stored price is invalid for subscription id=" + subscription.getId());
            }
        }

        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .customerId(customerId)     // Long expected in DTO
                .planType(subscription.getPlanType())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .price(price)              // Double expected in DTO
                .build();
    }

    // Convert DTO -> Entity (before saving; entity stores encrypted String)
    private Subscription mapToEntity(SubscriptionDTO dto) {
        return Subscription.builder()
                .id(dto.getId())
                // if dto.customerId is null, store null (so converter won't encrypt)
                .customerId(dto.getCustomerId() != null ? String.valueOf(dto.getCustomerId()) : null)
                .planType(dto.getPlanType())
                .status(dto.getStatus())
                // price set in create/update flows usually; but convert if present
                .price(dto.getPrice() != null ? String.valueOf(dto.getPrice()) : null)
                .build();
    }

}
