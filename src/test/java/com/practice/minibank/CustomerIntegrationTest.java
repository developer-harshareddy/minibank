
package com.practice.minibank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.practice.minibank.dto.CustomerResponse;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

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
}
