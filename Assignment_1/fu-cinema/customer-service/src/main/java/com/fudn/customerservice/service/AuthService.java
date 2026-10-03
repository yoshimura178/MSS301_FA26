package com.fudn.customerservice.service;

import com.fudn.customerservice.dto.LoginRequest;
import com.fudn.customerservice.dto.LoginResponse;
import com.fudn.customerservice.exception.ApiException;
import com.fudn.customerservice.model.Customer;
import com.fudn.customerservice.model.CustomerStatus;
import com.fudn.customerservice.repository.CustomerRepository;
import com.fudn.customerservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public LoginResponse login(LoginRequest request) {
        if (adminEmail.equals(request.email()) && adminPassword.equals(request.password())) {
            String token = jwtService.generateToken(0L, adminEmail, "ADMIN");
            return new LoginResponse(token, "Bearer", 3600, "ADMIN", 0L, adminEmail, "System Admin");
        }

        Customer customer = customerRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), customer.getPassword())) {
            throw ApiException.unauthorized("Invalid email or password");
        }

        if (customer.getCustomerStatus() == CustomerStatus.INACTIVE) {
            throw ApiException.forbidden("Account is inactive");
        }

        String token = jwtService.generateToken(customer.getCustomerId(), customer.getEmail(), "CUSTOMER");
        return new LoginResponse(token, "Bearer", 3600, "CUSTOMER", customer.getCustomerId(), customer.getEmail(), customer.getCustomerName());
    }
}
