package com.fudn.customerservice.dto;

import com.fudn.customerservice.model.CustomerStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AdminCustomerRequest(
        @NotBlank(message = "Customer name is required") @Size(max = 100) String customerName,
        @NotBlank(message = "Telephone is required") @Pattern(regexp = "^0\\d{9}$") String telephone,
        @NotBlank(message = "Email is required") @Email String email,
        @NotNull(message = "Birthday is required") @Past LocalDate customerBirthday,
        String password,
        @NotNull(message = "Status is required") CustomerStatus customerStatus) {
}
