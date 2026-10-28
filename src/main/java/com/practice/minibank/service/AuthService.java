
package com.practice.minibank.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practice.minibank.dto.LoginRequest;
import com.practice.minibank.dto.LoginResponse;
import com.practice.minibank.dto.RegisterRequest;
import com.practice.minibank.dto.RegisteredUserResponse;
import com.practice.minibank.entity.AppUserEntity;
import com.practice.minibank.exception.DuplicateUserEmailException;
import com.practice.minibank.exception.InvalidCredentialsException;
import com.practice.minibank.exception.InvalidPasswordException;
import com.practice.minibank.repository.AppUserRepository;

@Service
public class AuthService {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public RegisteredUserResponse register(RegisterRequest request) {

		String email = request.email().trim().toLowerCase(Locale.ROOT);
		String fullName = request.fullName().trim();

		if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
			throw new InvalidPasswordException();
		}

		if (appUserRepository.existsByEmail(email)) {
			throw new DuplicateUserEmailException();
		}

		String passwordHash = passwordEncoder.encode(request.password());
		AppUserEntity user = new AppUserEntity(fullName, email, passwordHash);

		try {
			AppUserEntity saved = appUserRepository.saveAndFlush(user);
			return new RegisteredUserResponse(saved.getId(), saved.getFullName(), saved.getEmail());

		} catch (DataIntegrityViolationException ex) {
			if (isEmailUniqueViolation(ex)) {
				throw new DuplicateUserEmailException();
			}
			throw ex;
		}
	}

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {

		String email = request.email().trim().toLowerCase(Locale.ROOT);

		AppUserEntity user = appUserRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

		if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new InvalidCredentialsException();
		}

		String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());

		return new LoginResponse(accessToken, "Bearer", jwtService.getAccessTokenLifetimeSeconds());
	}

	private boolean isEmailUniqueViolation(Throwable ex) {
		for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
			if (cause instanceof ConstraintViolationException cve) {
				return "uk_app_users_email".equalsIgnoreCase(cve.getConstraintName());
			}
		}
		return false;
	}
}
