package com.example.customer_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object representing a Customer")
public class CustomerDTO {

    @Schema(description = "Unique identifier for the customer", example = "1")
    private Long id;

    @Schema(description = "Full name of the customer", example = "Rana Sangati")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Schema(description = "Email address of the customer", example = "rana@example.com")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Schema(description = "10-digit phone number of the customer", example = "9876543210")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    @Schema(description = "Address of the customer", example = "Hyderabad, India")
    @NotBlank(message = "Address cannot be blank")
    private String address;
}
