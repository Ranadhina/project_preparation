package com.practice.Subscription.service;

import com.practice.Subscription.enums.PlanType;
import com.practice.Subscription.dto.SubscriptionDTO;
import com.practice.Subscription.exception.ResourceNotFoundException;
import com.practice.Subscription.model.Subscription;
import com.practice.Subscription.repo.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private Subscription subscription;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        subscription = Subscription.builder()
                .id(1L)
                .customerId(1L)
                .planType(PlanType.MONTHLY)
                .status("ACTIVE")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .price(499.0)
                .build();
    }

    // ✅ Test createSubscription()
    @Test
    void testCreateSubscription_Success() {
        SubscriptionDTO dto = SubscriptionDTO.builder()
                .customerId(1L)
                .planType(PlanType.MONTHLY)
                .status("ACTIVE")
                .build();

        when(subscriptionRepository.findAll()).thenReturn(List.of());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> {
            Subscription sub = invocation.getArgument(0);
            sub.setId(1L);
            return sub;
        });

        SubscriptionDTO result = subscriptionService.createSubscription(dto);

        assertNotNull(result.getId());
        assertEquals(PlanType.MONTHLY, result.getPlanType());
        verify(subscriptionRepository, times(1)).save(any());
    }

    // ✅ Test getSubscriptionById()
    @Test
    void testGetSubscriptionById_Success() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        SubscriptionDTO result = subscriptionService.getSubscriptionById(1L);

        assertEquals(1L, result.getId());
        assertEquals(PlanType.MONTHLY, result.getPlanType());
        verify(subscriptionRepository, times(1)).findById(1L);
    }

    // ❌ Test getSubscriptionById() when not found
    @Test
    void testGetSubscriptionById_NotFound() {
        when(subscriptionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                subscriptionService.getSubscriptionById(99L));
    }

    // ⚙️ Test updateSubscription() when subscription is active
    @Test
    void testUpdateSubscription_WhenActive_ShouldThrowError() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        SubscriptionDTO updateDto = SubscriptionDTO.builder()
                .customerId(1L)
                .planType(PlanType.QUARTERLY)
                .status("ACTIVE")
                .build();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                subscriptionService.updateSubscription(1L, updateDto));

        assertTrue(ex.getMessage().contains("Cannot update subscription"));
    }

    // ✅ Test deleteSubscription()
    @Test
    void testDeleteSubscription_Success() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        subscriptionService.deleteSubscription(1L);

        verify(subscriptionRepository, times(1)).delete(subscription);
    }
}
