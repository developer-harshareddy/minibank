
package com.practice.minibank.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class CustomerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String fullName;

	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@Column(nullable = false, length = 10)
	private String phoneNumber;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private Instant createdAt;

	protected CustomerEntity() {
		// Required by JPA
	}

	public CustomerEntity(String fullName, String email, String phoneNumber) {
		this.fullName = fullName;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

	public Long getId() {
		return id;
	}

	public String getFullName() {
		return fullName;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
