package com.practice.minibank.dto;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {
}