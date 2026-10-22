
package com.practice.minibank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.practice.minibank.dto.CustomerResponse;
import com.practice.minibank.repository.CustomerRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CustomerRepository customerRepository;

	@Test
	void shouldRegisterCustomerInPostgresAndRetrieveIt() throws Exception {

		String email = "integration-" + UUID.randomUUID() + "@example.com";

		String requestJson = objectMapper
				.writeValueAsString(new CustomerRequest("Integration Test Customer", email, "9876543210"));

		MvcResult postResult = mockMvc
				.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.email").value(email)).andReturn();

		CustomerResponse response = objectMapper.readValue(postResult.getResponse().getContentAsString(),
				CustomerResponse.class);

		long customerId = response.id();

		mockMvc.perform(get("/api/v1/customers/{id}", customerId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(customerId))
				.andExpect(jsonPath("$.fullName").value("Integration Test Customer"))
				.andExpect(jsonPath("$.email").value(email)).andExpect(jsonPath("$.phoneNumber").value("9876543210"));
	}

	@Test
	void shouldRejectDuplicateEmail() throws Exception {

		String email = "duplicate-" + UUID.randomUUID() + "@example.com";

		String requestJson = objectMapper
				.writeValueAsString(new CustomerRequest("Duplicate Test Customer", email, "9876543210"));

		mockMvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isConflict());
	}

	private record CustomerRequest(String fullName, String email, String phoneNumber) {
	}

	@Test
	void shouldAllowOnlyOneRegistrationForConcurrentDuplicateEmails() throws Exception {

		String email = "concurrent-" + UUID.randomUUID() + "@example.com";

		String requestJson = objectMapper
				.writeValueAsString(new CustomerRequest("Concurrent Test Customer", email, "9876543210"));

		CountDownLatch ready = new CountDownLatch(2);
		CountDownLatch start = new CountDownLatch(1);

		ExecutorService executor = Executors.newFixedThreadPool(2);

		try {
			java.util.concurrent.Callable<Integer> registration = () -> {
				ready.countDown();

				if (!start.await(10, TimeUnit.SECONDS)) {
					throw new IllegalStateException("Timed out waiting to start");
				}

				return mockMvc
						.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON).content(requestJson))
						.andReturn().getResponse().getStatus();
			};

			Future<Integer> first = executor.submit(registration);
			Future<Integer> second = executor.submit(registration);

			assertTrue(ready.await(10, TimeUnit.SECONDS), "Both requests should be ready");

			// Release both requests as close together as possible.
			start.countDown();

			int firstStatus = first.get(30, TimeUnit.SECONDS);
			int secondStatus = second.get(30, TimeUnit.SECONDS);

			assertNotEquals(firstStatus, secondStatus, "Exactly one request should succeed");

			assertTrue(Set.of(201, 409).contains(firstStatus));
			assertTrue(Set.of(201, 409).contains(secondStatus));

			assertEquals(1L, customerRepository.countByEmail(email), "Exactly one customer should be stored");

		} finally {
			start.countDown();
			executor.shutdownNow();
		}
	}

}
