package com.example.customer_service.service;

import com.example.customer_service.dto.CustomerDTO;
import com.example.customer_service.exception.ResourceNotFoundException;
import com.example.customer_service.model.Customer;
import com.example.customer_service.repo.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerDTO createCustomer(CustomerDTO dto) {
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Customer with email " + dto.getEmail() + " already exists");
        }

        log.info("Creating new customer: {}", dto.getEmail());
        Customer customer = mapToEntity(dto);
        Customer saved = customerRepository.save(customer);
        log.debug("Customer created successfully with ID {}", saved.getId());
        return mapToDTO(saved);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        log.info("Fetching customer with id {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapToDTO(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        log.info("Updating customer with id {}", id);
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setAddress(dto.getAddress());

        Customer updated = customerRepository.save(existing);
        log.debug("Customer updated successfully for id {}", id);
        return mapToDTO(updated);
    }

    @Transactional
    @Override
    public void deleteCustomer(Long id) {
        log.warn("Deleting customer with id {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
        log.info("Customer deleted successfully with id {}", id);
    }

    // Helper mappers
    private CustomerDTO mapToDTO(Customer c) {
        return CustomerDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .build();
    }

    private Customer mapToEntity(CustomerDTO dto) {
        return Customer.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build();
    }
}
