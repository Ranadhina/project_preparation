    package com.practice.Subscription.controller;

    import com.practice.Subscription.dto.SubscriptionDTO;
    import com.practice.Subscription.service.SubscriptionService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.media.Content;
    import io.swagger.v3.oas.annotations.media.Schema;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/subscriptions")
    @Tag(name = "Subscription Controller", description = "APIs for managing subscriptions")
    public class SubscriptionController {

        @Autowired
        private SubscriptionService subscriptionService;

        @Operation(
                summary = "Create a new subscription",
                description = "Creates a new subscription for a given customer and plan type",
                responses = {
                        @ApiResponse(responseCode = "201", description = "Subscription created successfully",
                                content = @Content(schema = @Schema(implementation = SubscriptionDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate subscription")
                }
        )
        @PostMapping
        public ResponseEntity<?> createSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO) {
            try {
                SubscriptionDTO created = subscriptionService.createSubscription(subscriptionDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }

        @Operation(
                summary = "Fetch all subscriptions",
                description = "Retrieve all subscriptions in the system",
                responses = {
                        @ApiResponse(responseCode = "200", description = "List of subscriptions fetched successfully")
                }
        )
        @GetMapping
        public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
            List<SubscriptionDTO> list = subscriptionService.getAllSubscriptions();
            return ResponseEntity.ok(list);
        }

        @Operation(
                summary = "Get subscription by ID",
                description = "Retrieve subscription details by subscription ID",
                responses = {
                        @ApiResponse(responseCode = "200", description = "Subscription found",
                                content = @Content(schema = @Schema(implementation = SubscriptionDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Subscription not found")
                }
        )
        @GetMapping("/{id}")
        public ResponseEntity<SubscriptionDTO> getSubscriptionById(@PathVariable Long id) {
            SubscriptionDTO dto = subscriptionService.getSubscriptionById(id);
            return ResponseEntity.ok(dto);
        }

        @Operation(
                summary = "Update a subscription",
                description = "Updates an existing subscription based on ID",
                responses = {
                        @ApiResponse(responseCode = "200", description = "Subscription updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Subscription not found")
                }
        )
        @PutMapping("/{id}")
        public ResponseEntity<SubscriptionDTO> updateSubscription(
                @PathVariable Long id,
                @Valid @RequestBody SubscriptionDTO subscriptionDTO
        ) {
            SubscriptionDTO updated = subscriptionService.updateSubscription(id, subscriptionDTO);
            return ResponseEntity.ok(updated);
        }

        @Operation(
                summary = "Delete subscription",
                description = "Deletes a subscription by its ID",
                responses = {
                        @ApiResponse(responseCode = "200", description = "Subscription deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Subscription not found")
                }
        )
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteSubscription(@PathVariable Long id) {
            subscriptionService.deleteSubscription(id);
            return ResponseEntity.ok("Subscription deleted successfully");
        }
    }
