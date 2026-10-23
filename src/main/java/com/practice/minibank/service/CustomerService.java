
package com.practice.minibank.service;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practice.minibank.dto.CreateCustomerRequest;
import com.practice.minibank.dto.CustomerResponse;
import com.practice.minibank.entity.CustomerEntity;
import com.practice.minibank.exception.CustomerNotFoundException;
import com.practice.minibank.exception.DuplicateEmailException;
import com.practice.minibank.repository.CustomerRepository;

@Service
public class CustomerService {

	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Transactional
	public CustomerResponse registerCustomer(CreateCustomerRequest request) {

		String email = request.email().trim().toLowerCase(Locale.ROOT);

		if (customerRepository.existsByEmail(email)) {
			throw new DuplicateEmailException(email);
		}

		CustomerEntity customer = new CustomerEntity(request.fullName().trim(), email, request.phoneNumber().trim());

		try {
			CustomerEntity saved = customerRepository.saveAndFlush(customer);

			return toResponse(saved);

		} catch (DataIntegrityViolationException ex) {
			if (isEmailUniqueViolation(ex)) {
				throw new DuplicateEmailException(email);
			}

			// Do not misclassify other database errors.
			throw ex;
		}

	}

	@Transactional(readOnly = true)
	public CustomerResponse getCustomerById(Long id) {

		CustomerEntity customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));

		return toResponse(customer);
	}

	private CustomerResponse toResponse(CustomerEntity customer) {
		return new CustomerResponse(customer.getId(), customer.getFullName(), customer.getEmail(),
				customer.getPhoneNumber());
	}

	private boolean isEmailUniqueViolation(Throwable ex) {
		for (Throwable cause = ex; cause != null; cause = cause.getCause()) {

			if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
				return "uk_customers_email".equalsIgnoreCase(cve.getConstraintName());
			}
		}

		return false;
	}
}
