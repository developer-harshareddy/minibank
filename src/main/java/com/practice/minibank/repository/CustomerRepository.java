
package com.practice.minibank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.minibank.entity.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

	boolean existsByEmail(String email);

	Optional<CustomerEntity> findByEmail(String email);

	long countByEmail(String email);
}
