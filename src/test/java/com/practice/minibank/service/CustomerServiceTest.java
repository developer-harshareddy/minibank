
package com.practice.minibank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.practice.minibank.dto.CreateCustomerRequest;
import com.practice.minibank.dto.CustomerResponse;
import com.practice.minibank.entity.CustomerEntity;
import com.practice.minibank.exception.CustomerNotFoundException;
import com.practice.minibank.exception.DuplicateEmailException;
import com.practice.minibank.repository.CustomerRepository;

class CustomerServiceTest {

	private CustomerRepository customerRepository;
	private CustomerService customerService;

	@BeforeEach
	void setUp() {
		customerRepository = org.mockito.Mockito.mock(CustomerRepository.class);
		customerService = new CustomerService(customerRepository);
	}

	@Test
	void shouldRegisterCustomerSuccessfully() {
		when(customerRepository.existsByEmail("ravi@example.com")).thenReturn(false);

		when(customerRepository.saveAndFlush(any(CustomerEntity.class))).thenAnswer(invocation -> {
			CustomerEntity entity = invocation.getArgument(0);
			ReflectionTestUtils.setField(entity, "id", 1L);
			return entity;
		});

		CustomerResponse response = customerService
				.registerCustomer(new CreateCustomerRequest("Ravi Kumar", "RAVI@EXAMPLE.COM", "9876543210"));

		assertEquals(1L, response.id());
		assertEquals("Ravi Kumar", response.fullName());
		assertEquals("ravi@example.com", response.email());
		verify(customerRepository).saveAndFlush(any(CustomerEntity.class));
	}

	@Test
	void shouldRejectDuplicateEmail() {
		when(customerRepository.existsByEmail("ravi@example.com")).thenReturn(true);

		assertThrows(DuplicateEmailException.class, () -> customerService
				.registerCustomer(new CreateCustomerRequest("Ravi Kumar", "RAVI@EXAMPLE.COM", "9876543210")));

		verify(customerRepository, never()).saveAndFlush(any(CustomerEntity.class));
	}

	@Test
	void shouldRetrieveExistingCustomer() {
		CustomerEntity entity = new CustomerEntity("Ravi Kumar", "ravi@example.com", "9876543210");

		ReflectionTestUtils.setField(entity, "id", 1L);

		when(customerRepository.findById(1L)).thenReturn(Optional.of(entity));

		CustomerResponse response = customerService.getCustomerById(1L);

		assertEquals(1L, response.id());
		assertEquals("ravi@example.com", response.email());
	}

	@Test
	void shouldThrowWhenCustomerDoesNotExist() {
		when(customerRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(999L));
	}
}
