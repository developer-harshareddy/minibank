
package com.practice.minibank.service;

import java.time.Instant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private static final long ACCESS_TOKEN_LIFETIME_SECONDS = 900;

	private final JwtEncoder jwtEncoder;

	public JwtService(JwtEncoder jwtEncoder) {
		this.jwtEncoder = jwtEncoder;
	}

	public String generateAccessToken(Long userId, String email) {
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plusSeconds(ACCESS_TOKEN_LIFETIME_SECONDS);

		JwtClaimsSet claims = JwtClaimsSet.builder().issuer("minibank").subject(userId.toString()).issuedAt(issuedAt)
				.expiresAt(expiresAt).claim("email", email).build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	public long getAccessTokenLifetimeSeconds() {
		return ACCESS_TOKEN_LIFETIME_SECONDS;
	}
}
