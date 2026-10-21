
package com.practice.minibank.service;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.practice.minibank.dto.CreateCustomerRequest;
import com.practice.minibank.dto.CustomerResponse;
import com.practice.minibank.exception.CustomerNotFoundException;
import com.practice.minibank.exception.DuplicateEmailException;
import com.practice.minibank.model.Customer;

@Service
public class CustomerService {

	private final ConcurrentMap<Long, Customer> customers = new ConcurrentHashMap<>();

	private final ConcurrentMap<String, Long> emailIndex = new ConcurrentHashMap<>();

	private final AtomicLong idGenerator = new AtomicLong(0);

	public synchronized CustomerResponse registerCustomer(CreateCustomerRequest request) {

		String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

		if (emailIndex.containsKey(normalizedEmail)) {
			throw new DuplicateEmailException(normalizedEmail);
		}

		long id = idGenerator.incrementAndGet();

		Customer customer = new Customer(id, request.fullName().trim(), normalizedEmail, request.phoneNumber().trim());

		customers.put(id, customer);
		emailIndex.put(normalizedEmail, id);

		return toResponse(customer);
	}

	public CustomerResponse getCustomerById(Long id) {
		Customer customer = customers.get(id);

		if (customer == null) {
			throw new CustomerNotFoundException(id);
		}

		return toResponse(customer);
	}

	private CustomerResponse toResponse(Customer customer) {
		return new CustomerResponse(customer.id(), customer.fullName(), customer.email(), customer.phoneNumber());
	}
}
