
package com.practice.minibank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.practice.minibank.dto.CreateCustomerRequest;
import com.practice.minibank.dto.CustomerResponse;
import com.practice.minibank.exception.CustomerNotFoundException;
import com.practice.minibank.exception.DuplicateEmailException;

class CustomerServiceTest {

	private CustomerService customerService;

	@BeforeEach
	void setUp() {
		customerService = new CustomerService();
	}

	@Test
	void shouldRegisterCustomerSuccessfully() {
		CreateCustomerRequest request = new CreateCustomerRequest("Ravi Kumar", "ravi@example.com", "9876543210");

		CustomerResponse response = customerService.registerCustomer(request);

		assertEquals(1L, response.id());
		assertEquals("Ravi Kumar", response.fullName());
		assertEquals("ravi@example.com", response.email());
	}

	@Test
	void shouldNormalizeEmailToLowercase() {
		CreateCustomerRequest request = new CreateCustomerRequest("Ravi Kumar", "RAVI@EXAMPLE.COM", "9876543210");

		CustomerResponse response = customerService.registerCustomer(request);

		assertEquals("ravi@example.com", response.email());
	}

	@Test
	void shouldRejectDuplicateEmail() {
		CreateCustomerRequest first = new CreateCustomerRequest("Ravi Kumar", "ravi@example.com", "9876543210");

		CreateCustomerRequest second = new CreateCustomerRequest("Another Customer", "RAVI@EXAMPLE.COM", "9123456780");

		customerService.registerCustomer(first);

		assertThrows(DuplicateEmailException.class, () -> customerService.registerCustomer(second));
	}

	@Test
	void shouldRetrieveExistingCustomer() {
		CreateCustomerRequest request = new CreateCustomerRequest("Ravi Kumar", "ravi@example.com", "9876543210");

		CustomerResponse created = customerService.registerCustomer(request);

		CustomerResponse found = customerService.getCustomerById(created.id());

		assertEquals(created.id(), found.id());
		assertEquals(created.email(), found.email());
	}

	@Test
	void shouldThrowExceptionWhenCustomerDoesNotExist() {
		assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(999L));
	}

	@Test
	void shouldAssignDifferentIdsToDifferentCustomers() {
		CustomerResponse first = customerService
				.registerCustomer(new CreateCustomerRequest("Ravi Kumar", "ravi@example.com", "9876543210"));

		CustomerResponse second = customerService
				.registerCustomer(new CreateCustomerRequest("Priya Kumar", "priya@example.com", "9123456780"));

		assertEquals(1L, first.id());
		assertEquals(2L, second.id());
	}
}
