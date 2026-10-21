
package com.practice.minibank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(

		@NotBlank(message = "Full name is required") @Size(max = 100, message = "Full name must not exceed 100 characters") String fullName,

		@NotBlank(message = "Email is required") @Email(message = "Enter a valid email address") @Size(max = 254, message = "Email must not exceed 254 characters") String email,

		@NotBlank(message = "Phone number is required") @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits") String phoneNumber) {
}
