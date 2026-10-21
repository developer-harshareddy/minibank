
package com.practice.minibank.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

	@GetMapping("/health")
	public Map<String, String> health() {
		return Map.of("application", "MiniBank", "status", "UP", "message", "MiniBank API is running");
	}
}
