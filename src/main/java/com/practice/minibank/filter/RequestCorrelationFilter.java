package com.practice.minibank.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestCorrelationFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(RequestCorrelationFilter.class);

	private static final String REQUEST_ID = "requestId";
	private static final String RESPONSE_HEADER = "X-Request-ID";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestId = UUID.randomUUID().toString();
		long startTime = System.nanoTime();

		try {
			MDC.put(REQUEST_ID, requestId);
			response.setHeader(RESPONSE_HEADER, requestId);

			filterChain.doFilter(request, response);
		} finally {
			long durationMs = (System.nanoTime() - startTime) / 1_000_000;

			log.info("HTTP request completed method={} path={} status={} durationMs={}", request.getMethod(),
					request.getRequestURI(), response.getStatus(), durationMs);

			MDC.remove(REQUEST_ID);
		}
	}
}
