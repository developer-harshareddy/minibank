
package com.practice.minibank.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.minibank.dto.CreateCustomerRequest;
import com.practice.minibank.dto.CustomerResponse;
import com.practice.minibank.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@PostMapping
	public ResponseEntity<CustomerResponse> registerCustomer(@Valid @RequestBody CreateCustomerRequest request) {

		CustomerResponse response = customerService.registerCustomer(request);

		URI location = URI.create("/api/v1/customers/" + response.id());

		return ResponseEntity.created(location).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {

		return ResponseEntity.ok(customerService.getCustomerById(id));
	}
}
