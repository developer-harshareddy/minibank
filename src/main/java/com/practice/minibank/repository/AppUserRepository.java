
package com.practice.minibank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.minibank.entity.AppUserEntity;

public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {

	Optional<AppUserEntity> findByEmail(String email);

	boolean existsByEmail(String email);
}
