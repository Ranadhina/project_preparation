package com.example.customer_service.controller;

import com.example.customer_service.dto.CustomerDTO;
import com.example.customer_service.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customer Controller", description = "APIs for managing customer information")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(
            summary = "Create a new customer",
            description = "Registers a new customer in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer created successfully",
                            content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid customer details")
            }
    )
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(customerService.createCustomer(dto));
    }

    @Operation(
            summary = "Get customer by ID",
            description = "Retrieve details of a customer by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer found",
                            content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @Operation(
            summary = "Get all customers",
            description = "Retrieve all registered customers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of customers fetched successfully")
            }
    )
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @Operation(
            summary = "Update a customer",
            description = "Update details of an existing customer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO dto
    ) {
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    @Operation(
            summary = "Delete a customer",
            description = "Deletes a customer by their ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
